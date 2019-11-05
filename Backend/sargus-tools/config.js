'use strict'

const debug = require('debug')('sargus:api:db')
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

// Objeto de configuración de la Base de Datos
function config_db(init) {
    return {
        db: {
            database: process.env.DB_NAME || 'sargus',          // Nombre de la DB  
            username: process.env.DB_USER || 'qbo3d',           // Usuario          
            password: process.env.DB_PASS || '.Sargus123.*',    // Contraseña       
            local: process.env.DB_HOST || 'localhost',          // Dirección IP     
            dialect: 'postgres',                                   // Nombre del gestor de DBs a usar en el proyecto 
            logging: s => debug(s),
            setup: init                                         // Restauración de la Database 
        },
        auth: {
            secret: process.env.SECRET || 'qbo3d'
        }
    }
}

function config_proxy() {
    return {
        proxy: {
            endpoint: process.env.API_ENDPOINT || 'http://localhost:3000',
            server_host: process.env.SERVER_HOST || 'http://localhost:8080',
            apiToken: process.env.API_TOKEN || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbiI6dHJ1ZSwidXNlcm5hbWUiOiJxYm8zZCIsInBlcm1pc3Npb25zIjpbIm1ldHJpY3M6cmVhZCJdLCJpYXQiOjE1NTU4MDE3MDV9.FeoNSoUvMBfLUzjNz58GAWcKJlI2Y-qx0c5Qg30-66k'
        }
    }
}

module.exports = {
    config_db,
    config_proxy,
    optimization: {
        minimize: true,
        minimizer: [new UglifyJsPlugin({
            parallel: true,
            terserOptions: {
              ecma: 6,
            },
            compress:{
                drop_console: true
            },
            test: /\.js(\?.*)?$/i,
          })],
      },
}