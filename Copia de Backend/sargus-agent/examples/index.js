const SargusAgent = require('../')

const agent = new SargusAgent({
    name: 'myapp',
    username: 'admin',
    interval: 2000
})

agent.addMetric('rss', function getRss() { // Rss: Resitend set size (dato de memoria)
    return process.memoryUsage().rss // process.memoryUsage() es una función que me muestra la informacion de uso de memoria del proceso
})

agent.addMetric('promiseMetric', function getRandomPromise() {
    return Promise.resolve(Math.random())
})

agent.addMetric('callbackMetric', function getRandomCallback(callback) {
    setTimeout(() => {
        callback(null, Math.random())
    }, 1000)
})

agent.connect()

// This agent only
agent.on('connected', handler)
agent.on('disconnected', handler)
agent.on('message', handler)

// Other Agents
agent.on('agent/connected', handler)
agent.on('agent/disconnected', handler)
agent.on('agent/message', handler)

function handler(payload) {
    console.log(payload)
}

// setTimeout(() => agent.disconnect(), 10000)
