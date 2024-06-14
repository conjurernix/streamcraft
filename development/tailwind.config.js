const presetQuick = require("franken-ui/shadcn-ui/preset-quick");
const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
    content: ["./resources/admin/public/js/**/*.js", "./resources/client/public/js/**/*.js"],
    theme: {
        extend: {
            fontFamily: {
                sans: ["Inter var", ...defaultTheme.fontFamily.sans],
            },
        },
    },
    plugins: [
        require('@tailwindcss/forms'),
    ],
    presets: [presetQuick()]
}