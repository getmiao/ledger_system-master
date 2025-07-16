const { defineConfig } = require("@vue/cli-service");
const webpack = require("webpack");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      "/api": {
        target: "http://172.16.70.66:8101",
        changeOrigin: true,
      },
    },
    client: {
      overlay: false,
    },
  },
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: "false",
        __VUE_PROD_DEVTOOLS__: "false",
        __VUE_OPTIONS_API__: "true",
        __VUE_PROD_TIPS__: "false",
      }),
    ],
  },
});
