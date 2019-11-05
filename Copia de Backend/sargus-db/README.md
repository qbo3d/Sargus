# sargus-db

## Usage

``` js
const setupDatabase = require('sargus-db')

setupDabase(config).then(db => {
  const { Agent, Metric } = db

}).catch(err => console.error(err))
```
