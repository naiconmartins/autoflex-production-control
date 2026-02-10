import { defineConfig } from "cypress";
import { webpack } from "next/dist/compiled/webpack/webpack";
import path from "path";

export default defineConfig({
  e2e: {
    baseUrl: "http://localhost:3000",
    supportFile: "cypress/support/e2e.ts",
    specPattern: "cypress/e2e/**/*.cy.{ts,tsx}",
  },
  component: {
    devServer: {
      framework: "next",
      bundler: "webpack",
      webpackConfig: {
        resolve: {
          alias: {
            "@": path.resolve(__dirname, "."),
            "next/headers": path.resolve(__dirname, "cypress/mocks/next-headers.ts"),
            "@/actions/findCookie": path.resolve(
              __dirname,
              "cypress/mocks/findCookie.ts",
            ),
            "@/actions/auth.actions": path.resolve(
              __dirname,
              "cypress/mocks/auth.actions.ts",
            ),
            "@/actions/product.actions": path.resolve(
              __dirname,
              "cypress/mocks/product.actions.ts",
            ),
            "@/actions/raw-material.actions": path.resolve(
              __dirname,
              "cypress/mocks/raw-material.actions.ts",
            ),
            "@/actions/production-capacity.actions": path.resolve(
              __dirname,
              "cypress/mocks/production-capacity.actions.ts",
            ),
            "@/store/thunks/product.thunks": path.resolve(
              __dirname,
              "cypress/mocks/product.thunks.ts",
            ),
            "@/store/thunks/raw-material.thunks": path.resolve(
              __dirname,
              "cypress/mocks/raw-material.thunks.ts",
            ),
            "@/store/thunks/production-capacity.thunks": path.resolve(
              __dirname,
              "cypress/mocks/production-capacity.thunks.ts",
            ),
          },
        },
        module: {
          rules: [
            {
              test: /\.css$/,
              use: ["style-loader", "css-loader", "postcss-loader"],
            },
          ],
        },
        plugins: [
          new webpack.NormalModuleReplacementPlugin(
            /actions\/findCookie$/,
            path.resolve(__dirname, "cypress/mocks/findCookie.ts"),
          ),
          new webpack.NormalModuleReplacementPlugin(
            /actions\/findCookie\.ts$/,
            path.resolve(__dirname, "cypress/mocks/findCookie.ts"),
          ),
          new webpack.DefinePlugin({
            "process.env.API_URL": JSON.stringify("http://localhost:8080"),
          }),
        ],
      },
    },
  },
});
