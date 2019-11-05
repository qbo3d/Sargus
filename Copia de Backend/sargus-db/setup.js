'use strict'

const inquirer = require('inquirer')
const minimist = require('minimist')
const db = require('./')
const config = require('../sargus-tools/config').config_db(true)

const { handleFatalError } = require('../sargus-tools/utils')

const args = minimist(process.argv)
const prompt = inquirer.createPromptModule()

//Ejecutar el comendo sin confirmacion: node setup --yes
async function setup() {
    if (!args.yes) {
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

    await db(config.db).catch(handleFatalError)

    console.log('Success!')
    process.exit(0)
}

setup()
