'use strict'

const db = require('../')

const { handleFatalError } = require('../sargus-tools/utils')

async function run() {
    const config = {
        database: process.env.DB_NAME || 'sargus',
        username: process.env.DB_USER || 'qbo3d',
        password: process.env.DB_PASS || '.Sargus123.*',
        host: process.env.DB_HOST || 'localhost',
        dialect: 'mysql'
    }

    const { Agent, Metric } = await db(config).catch(handleFatalError)

    const agent = await Agent.createOrUpdate({
        uuid: 'yyx',
        name: 'test',
        username: 'test',
        hostname: 'test',
        pid: 1,
        connected: true
    }).catch(handleFatalError)

    console.log('--agent--')
    console.log(agent)

    const agents = await Agent.findAll().catch(handleFatalError)

    console.log('--agents--')
    console.log(agents)

    const metric = await Metric.create(agent.uuid, {
        type: 'memory',
        value: '300'
    }).catch(handleFatalError)

    console.log('--metric--')
    console.log(metric)

    const metrics = await Metric.findByAgentUuid(agent.uuid).catch(handleFatalError)

    console.log('--metrics--')
    console.log(metrics)

    const metricsByType = await Metric.findByTypeAgentUuid('memory', agent.uuid).catch(handleFatalError)
    console.log('--metrics--')
    console.log(metricsByType)
}

run()
