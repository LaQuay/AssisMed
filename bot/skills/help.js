//
// Command: help
//
module.exports = function (controller) {

    controller.hears([/^help$/], 'direct_message,direct_mention', function (bot, message) {
        var text = "Here are my skills:";
        text += "\n- " + bot.appendMention(message, "medicalevent") + ": create a new medical event";
        text += "\n- " + bot.appendMention(message, "prescribe") + ": prescribe new medication to user";
        text += "\n- " + bot.appendMention(message, "visits") + ": list all medical events of the user";
        text += "\n- " + bot.appendMention(message, "prescriptions") + ": list all the medication prescribed to the user";
        bot.reply(message, text);
    });
}
