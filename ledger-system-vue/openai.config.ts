const { generateService } = require("@umijs/openapi");

generateService({
  requestLibPath: "import request from '@/request'",
  schemaPath: "http://172.16.70.66:8101/api/v2/api-docs",
  serversPath: "./src",
});
