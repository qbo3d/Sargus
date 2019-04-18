const debug = require('debug')('sargus:db:setup')

// Objeto de configuración de la Base de Datos
module.exports = function (init = true) {
    return {
        database: process.env.DB_NAME || 'sargus', /* Nombre de la DB  */
        username: process.env.DB_USER || 'qbo3d',      /* Usuario          */
        password: process.env.DB_PASS || '.Sargus123.*',      /* Contraseña       */
        local: process.env.DB_HOST || 'localhost',      /* Dirección IP     */
        dialect: 'mysql', /* Nombre del gestor de DBs a usar en el proyecto */
        logging: s => debug(s),
        setup: init /* Restauración de la Database */
    }
}