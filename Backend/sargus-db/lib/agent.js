'use strict'

module.exports = function setupAgent(AgentModel) {
  async function createOrUpdate(agent) {
    const cond = {
      where: {
        uuid: agent.uuid
      }
    }

    const existAgent = await AgentModel.findOne(cond)

    if (existAgent) {
      const update = await AgentModel.update(agent, cond)
      return update ? AgentModel.findOne(cond) : existAgent
    }

    const result = await AgentModel.create(agent)
    return result.toJSON()
  }

  function findById(id) {
    return AgentModel.findById(id)
  }

  function findByUuid(uuid) {
    return AgentModel.findOne({
      where: {
        uuid
      }
    })
  }

  function findAll() {
    return AgentModel.findAll()
  }

  function findConnected() {
    return AgentModel.findAll({
      where: {
        connected: true
      }
    })
  }

  function findByUsername(username) {
    return AgentModel.findAll({
      where: {
        username,
        connected: true
      }
    })
  }

  return {
    createOrUpdate,
    findById,
    findByUuid,
    findAll,
    findConnected,
    findByUsername
  }
}