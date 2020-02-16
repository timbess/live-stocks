if (process.env.NODE_ENV === "production") {
    const opt = require("./react-scala-test-opt.js");
    opt.main();
    module.exports = opt;
} else {
    var exports = window;
    exports.require = require("./react-scala-test-fastopt-entrypoint.js").require;
    window.global = window;

    const fastOpt = require("./react-scala-test-fastopt.js");
    fastOpt.main()
    module.exports = fastOpt;

    if (module.hot) {
        module.hot.accept();
    }
}
