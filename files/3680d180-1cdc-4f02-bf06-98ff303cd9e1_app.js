const express = require('express');
const app = express();
const port = process.env.PORT || 3000;

app.get('/', (req, res) => {
  res.send('Hello, World! is it working ? and now ? this is the time maybe now ?');
});

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
