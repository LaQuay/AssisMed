module.exports = function (controller) {

    controller.hears([/^thanks$/], 'direct_message,direct_mention', function (bot, message) {
        bot.say("Don't mention it");
    });
}
