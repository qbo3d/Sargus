'use strict'

const debug = require('debug')('sargus:db:setup')
const inquirer = require('inquirer')
const chalk = require('chalk')
const db = require('./')
const config = require('./config-db')()

const prompt = inquirer.createPromptModule()

async function setup() {
    if (process.argv.pop() !== '--y') {
        const answer = await prompt([
            {
                type: 'confirm',
                name: 'setup',
                message: 'Esta acción destruirá la base de datos, esta seguro?'
            }
        ]).catch(handleFatalError)

        if (!answer.setup) {
            return console.log('No pasa nada :)')
        }
    }

    // const config = {
    //     database: process.env.DB_NAME || 'sargus',
    //     username: process.env.DB_USER || 'qbo3d',
    //     password: process.env.DB_PASS || '.Sargus123.*',
    //     host: process.env.DB_HOST || 'localhost',
    //     dialect: 'mysql',
    //     loggin: s => debug(s),
    //     setup: true
    // }

    await db(config).catch(handleFatalError)

    console.log('Success!')
    process.exit(0)
}

function handleFatalError(err) {
    console.error(`${chalk.red('[fatal error]')} ${err.message}`)
    console.error(err.stack)
    process.exit(1)
}

setup()
