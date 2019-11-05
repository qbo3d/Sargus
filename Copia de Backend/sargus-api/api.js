'use strict'

const debug = require('debug')('sargus:api:routes')
const express = require('express')
const asyncify = require('express-asyncify')
const auth = require('express-jwt')
const guard = require('express-jwt-permissions')()
const db = require('sargus-db')
const config = require('../sargus-tools/config').config_db(false)
const auth_token = require('./auth')
const util = require('util')
const sing = util.promisify(auth_token.sign)

const Errors = require('../sargus-tools/errors')

const api = asyncify(express.Router())

let services, Agent, Metric

api.use('*', async (req, res, next) => {
  if (!services) {
    debug('Connecting to database')
    try {
      services = await db(config.db)
    } catch (e) {
      return next(e)
    }

    Agent = services.Agent
    Metric = services.Metric
  }
  next()
})

api.get('/auth/:username', async (req, res, next) => {
  debug('request has me to /agents')

  const {username} = req.params

  let token = await sing({
    admin: true,
    username: username,
    permissions: [
      'metrics:read'
    ]
  }, config.auth.secret)

  if (!token) {
    return next(new Errors.NotAuthorizedError())
  }

  res.send({token})
  // res.send({username})
})

api.get('/agents', auth(config.auth), guard.check(['metrics:read']), async (req, res, next) => {
  debug('request has me to /agents')

  const { user } = req

  if (!user || !user.username) {
    return next(new Error('Not authorized'))
  }

  let agents = []

  try {
    if (user.admin) {
      agents = await Agent.findConnected()
    } else {
      agents = await Agent.findByUsername(user.username)
    }
  } catch (e) {
    return next(e)
  }

  res.send(agents)
})

api.get('/agent/:uuid', auth(config.auth), guard.check(['metrics:read']), async (req, res, next) => {
  const { uuid } = req.params

  debug(`request to /agent/${uuid}`)

  let agent
  try {
    agent = await Agent.findByUuid(uuid)
  } catch (e) {
    return next(e)
  }

  if (!agent) {
    return next(new Errors.AgentNotFoundError(uuid))
  }

  res.send(agent)
})

api.get('/metrics/:uuid', auth(config.auth), guard.check(['metrics:read']), async (req, res, next) => {
  const { uuid } = req.params

  debug(`request has me to /metrics/${uuid}`)

  let metrics = []
  try {
    metrics = await Metric.findByAgentUuid(uuid)
  } catch (e) {
    return next(e)
  }

  if (!metrics || metrics.length === 0) {
    return next(new Errors.MetricsNotFoundError(uuid))
  }

  res.send(metrics)
})

api.get('/metrics/:uuid/:type', auth(config.auth), guard.check(['metrics:read']), async (req, res, next) => {
  const { uuid, type } = req.params

  debug(`request has me to /metrics/${uuid}/${type}`)

  let metrics = []
  try {
    metrics = await Metric.findByTypeAgentUuid(uuid, type)
  } catch (e) {
    return next(e)
  }

  if (!metrics || metrics.length === 0) {
    return next(new Errors.MetricsNotFoundError(uuid, type))
  }

  res.send(metrics)
})

module.exports = api