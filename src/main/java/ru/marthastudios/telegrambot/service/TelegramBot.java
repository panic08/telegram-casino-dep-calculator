package ru.marthastudios.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.marthastudios.telegrambot.configuration.BotConfig;
import ru.marthastudios.telegrambot.entity.Casino;
import ru.marthastudios.telegrambot.service.impl.CasinoServiceImpl;
import ru.marthastudios.telegrambot.step.CasinoAddAndRemoveStep;
import ru.marthastudios.telegrambot.step.CasinoAddReplenishmentAndWithdrawalStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private CasinoServiceImpl casinoService;

    @Autowired
    private BotConfig botConfig;
    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private HashMap<Long, CasinoAddAndRemoveStep> casinoAddStep = new HashMap<>();
    private HashMap<Long, CasinoAddAndRemoveStep> casinoRemoveStep = new HashMap<>();
    private HashMap<Long, CasinoAddReplenishmentAndWithdrawalStep> casinoReplenishmentStep = new HashMap<>();
    private HashMap<Long, CasinoAddReplenishmentAndWithdrawalStep> casinoWithdrawalStep = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();


            switch (message){
                case "/start" -> {
                    SendMessage sendMessage = new SendMessage();

                    sendMessage.setText("Начните пользоваться ботом для подсчета депозитов/выводов в казино уже сейчас \uD83C\uDF52");
                    sendMessage.setChatId(chatId);

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                    InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                    inlineKeyboardButton1.setText("➕Добавить казино");
                    inlineKeyboardButton1.setCallbackData("add casino");

                    InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                    inlineKeyboardButton2.setText("➖Удалить казино");
                    inlineKeyboardButton2.setCallbackData("delete casino");

                    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
                    List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
                    List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
                    List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();

                    List<Casino> casinos = casinoService.getAllCasinoByChatId(chatId);

                    int iteration = 0;

                    for (Casino casino : casinos){

                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

                        if (iteration > 9){
                            inlineKeyboardButton.setText(casino.getName());
                            inlineKeyboardButton.setCallbackData(casino.getName());

                            keyboardButtonsRow4.add(inlineKeyboardButton);
                        } else if (iteration >= 5){
                            inlineKeyboardButton.setText(casino.getName());
                            inlineKeyboardButton.setCallbackData(casino.getName());

                            keyboardButtonsRow3.add(inlineKeyboardButton);
                        } else {
                            inlineKeyboardButton.setText(casino.getName());
                            inlineKeyboardButton.setCallbackData(casino.getName());

                            keyboardButtonsRow2.add(inlineKeyboardButton);
                        }

                        iteration+=1;
                    }

                    keyboardButtonsRow1.add(inlineKeyboardButton1);
                    keyboardButtonsRow1.add(inlineKeyboardButton2);


                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(keyboardButtonsRow1);
                    rowList.add(keyboardButtonsRow2);
                    rowList.add(keyboardButtonsRow3);
                    rowList.add(keyboardButtonsRow4);

                    inlineKeyboardMarkup.setKeyboard(rowList);

                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                    try {
                        execute(sendMessage);
                    } catch (Exception ignored){
                    }

                    return;
                }
            }

            if (casinoAddStep.get(chatId) != null){
                if (casinoAddStep.get(chatId).getStep() == 0){
                    Casino casino = new Casino();

                    casino.setChatId(chatId);
                    casino.setName(message);
                    casino.setProfit(0D);
                    casino.setSpent(0D);

                    casinoService.createCasino(casino);

                    SendMessage sendMessage = new SendMessage();

                    sendMessage.setChatId(chatId);
                    sendMessage.setText("✅Вы успешно создали казино: " + message);

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                    InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                    inlineKeyboardButton1.setText("⭕Назад в управление казино");
                    inlineKeyboardButton1.setCallbackData("back casino management");

                    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

                    keyboardButtonsRow1.add(inlineKeyboardButton1);

                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(keyboardButtonsRow1);

                    inlineKeyboardMarkup.setKeyboard(rowList);

                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                    try {
                        execute(sendMessage);
                    } catch (Exception ignored){
                    }

                    casinoAddStep.remove(chatId);

                    return;
                }
            }

            if (casinoRemoveStep.get(chatId) != null){
                if (casinoRemoveStep.get(chatId).getStep() == 0){
                    casinoService.deleteCasinoByNameAndChatId(message, chatId);

                    SendMessage sendMessage = new SendMessage();

                    sendMessage.setChatId(chatId);
                    sendMessage.setText("✅Вы успешно удалили казино: " + message);


                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                    InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                    inlineKeyboardButton1.setText("⭕Назад в управление казино");
                    inlineKeyboardButton1.setCallbackData("back casino management");

                    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

                    keyboardButtonsRow1.add(inlineKeyboardButton1);

                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(keyboardButtonsRow1);

                    inlineKeyboardMarkup.setKeyboard(rowList);

                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                    try {
                        execute(sendMessage);
                    } catch (Exception ignored){
                    }

                    casinoRemoveStep.remove(chatId);

                    return;
                }
            }

            if (casinoReplenishmentStep.get(chatId) != null){
                if (casinoReplenishmentStep.get(chatId).getStep() == 0){
                    Casino casino = casinoService.getCasinoByNameAndChatId(casinoReplenishmentStep.get(chatId).getCasinoName(), chatId);

                    casino.setSpent(casino.getSpent() + Double.parseDouble(message));

                    casinoService.createCasino(casino);

                    SendMessage sendMessage = new SendMessage();

                    sendMessage.setChatId(chatId);
                    sendMessage.setText("✅Вы успешно депозитнули в казино: " + casino.getName() + " на " + Double.parseDouble(message) + "₽");

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                    InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                    inlineKeyboardButton1.setText("⭕Назад в параметры казино " + casino.getName());
                    inlineKeyboardButton1.setCallbackData("back param " + casino.getName());

                    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

                    keyboardButtonsRow1.add(inlineKeyboardButton1);

                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(keyboardButtonsRow1);

                    inlineKeyboardMarkup.setKeyboard(rowList);

                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                    casinoReplenishmentStep.remove(chatId);

                    try {
                        execute(sendMessage);
                    } catch (Exception ignored){
                    }
                    return;
                }
            }

            if (casinoWithdrawalStep.get(chatId) != null){
                if (casinoWithdrawalStep.get(chatId).getStep() == 0){
                    Casino casino = casinoService.getCasinoByNameAndChatId(casinoWithdrawalStep.get(chatId).getCasinoName(), chatId);

                    casino.setProfit(casino.getProfit() + Double.parseDouble(message));

                    casinoService.createCasino(casino);

                    SendMessage sendMessage = new SendMessage();

                    sendMessage.setChatId(chatId);
                    sendMessage.setText("✅Вы успешно вывели из казино: " + casino.getName() + " на " + Double.parseDouble(message) + "₽");

                    casinoWithdrawalStep.remove(chatId);

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                    InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                    inlineKeyboardButton1.setText("⭕Назад в параметры казино " + casino.getName());
                    inlineKeyboardButton1.setCallbackData("back param " + casino.getName());

                    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

                    keyboardButtonsRow1.add(inlineKeyboardButton1);

                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(keyboardButtonsRow1);

                    inlineKeyboardMarkup.setKeyboard(rowList);

                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                    try {
                        execute(sendMessage);
                    } catch (Exception ignored){
                    }
                    return;
                }
            }


        }else if (update.hasCallbackQuery()){
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callBackData = update.getCallbackQuery().getData();


            if (callBackData.equals("add casino")){
                SendMessage sendMessage = new SendMessage();

                if (casinoService.getAllCasinoByChatId(chatId).size() >= 15){
                    sendMessage.setText("❌Вы превысили максимально количество казино, которые вы можете иметь (макс. число: 15). Удалите одно из них!");
                    sendMessage.setChatId(chatId);

                    try {
                        execute(sendMessage);
                    } catch (Exception ignored){
                    }

                    return;
                }

                sendMessage.setText("▶\uFE0FВведите название вашего казино");
                sendMessage.setChatId(chatId);

                casinoAddStep.put(chatId, new CasinoAddAndRemoveStep(0, null));

                try {
                    execute(sendMessage);
                } catch (Exception ignored){
                }

                return;
            }

            if (callBackData.equals("delete casino")){
                SendMessage sendMessage = new SendMessage();

                sendMessage.setText("▶\uFE0FВведите название казино, которые вы желаете удалить");
                sendMessage.setChatId(chatId);

                casinoRemoveStep.put(chatId, new CasinoAddAndRemoveStep(0, null));

                try {
                    execute(sendMessage);
                } catch (Exception ignored){
                }
                return;
            }

            if (callBackData.equals("check stat")){
                String casinoName = update.getCallbackQuery().getMessage().getText().split("Выберите один из вариантов действий над - ")[1];

                Casino casino = casinoService.getCasinoByNameAndChatId(casinoName, chatId);
                double profit = casino.getProfit() - casino.getSpent();
                    String result = profit > 0 ? "В плюсе" : "В минусе";

                EditMessageText editMessageText = new EditMessageText();

                    editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMessageText.setChatId(chatId);
                    editMessageText.setText("❗Название казино: " + casinoName + "\n✴Пополнений: " + casino.getSpent() + "₽" + "\n✅Выводов: " + casino.getProfit() + "₽" +
                            "\n\uD83D\uDCB2Профит: " + profit+ "₽" + "\n〰Результат: " + result);

                    EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();

                    editMessageReplyMarkup.setChatId(chatId);
                    editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                inlineKeyboardButton1.setText("⭕Назад в параметры казино " + casino.getName());
                inlineKeyboardButton1.setCallbackData("back param " + casino.getName());

                List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

                keyboardButtonsRow1.add(inlineKeyboardButton1);

                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(keyboardButtonsRow1);

                inlineKeyboardMarkup.setKeyboard(rowList);

                editMessageText.setReplyMarkup(inlineKeyboardMarkup);

                try {
                    execute(editMessageText);
                } catch (Exception ignored){
                }

                return;
            }

            if (callBackData.equals("add replenishment")){
                String casinoName = update.getCallbackQuery().getMessage().getText().split("Выберите один из вариантов действий над - ")[1];

                EditMessageText editMessageText = new EditMessageText();

                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setChatId(chatId);
                editMessageText.setText("▶Введите сумму, на которую вы совершили депозит в казино: " + casinoName);

                casinoReplenishmentStep.put(chatId, new CasinoAddReplenishmentAndWithdrawalStep(0, casinoName, 0));

                try {
                    execute(editMessageText);
                } catch (Exception ignored){
                }
            }

            if (callBackData.equals("add withdrawal")){
                String casinoName = update.getCallbackQuery().getMessage().getText().split("Выберите один из вариантов действий над - ")[1];

                EditMessageText editMessageText = new EditMessageText();

                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setChatId(chatId);
                editMessageText.setText("▶Введите сумму, на которую вы хотите вывести из казино: " + casinoName);

                casinoWithdrawalStep.put(chatId, new CasinoAddReplenishmentAndWithdrawalStep(0, casinoName, 0));

                try {
                    execute(editMessageText);
                } catch (Exception ignored){
                }
            }


            try {
                if (callBackData.equals("back param " + callBackData.split("back param ")[1])) {
                    String casinoName = callBackData.split("back param ")[1];

                    EditMessageText editMessageText = new EditMessageText();

                    editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMessageText.setChatId(chatId);
                    editMessageText.setText("Выберите один из вариантов действий над - " + casinoName);

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                    InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                    inlineKeyboardButton1.setText("➕Добавить пополнение");
                    inlineKeyboardButton1.setCallbackData("add replenishment");

                    InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                    inlineKeyboardButton2.setText("➕Добавить вывод");
                    inlineKeyboardButton2.setCallbackData("add withdrawal");

                    InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
                    inlineKeyboardButton3.setText("♾Просмотреть статистику");
                    inlineKeyboardButton3.setCallbackData("check stat");

                    InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
                    inlineKeyboardButton4.setText("⭕Назад в управление казино");
                    inlineKeyboardButton4.setCallbackData("back casino management");

                    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
                    List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
                    List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

                    keyboardButtonsRow1.add(inlineKeyboardButton1);
                    keyboardButtonsRow1.add(inlineKeyboardButton2);

                    keyboardButtonsRow2.add(inlineKeyboardButton3);

                    keyboardButtonsRow3.add(inlineKeyboardButton4);

                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(keyboardButtonsRow1);
                    rowList.add(keyboardButtonsRow2);
                    rowList.add(keyboardButtonsRow3);

                    inlineKeyboardMarkup.setKeyboard(rowList);


                    editMessageText.setReplyMarkup(inlineKeyboardMarkup);

                    try {
                        execute(editMessageText);
                    } catch (Exception ignored) {
                    }
                    return;
                }
            } catch (Exception ignored) {
            }

            if (callBackData.equals("back casino management")){
                EditMessageText editMessage = new EditMessageText();

                editMessage.setText("Начните пользоваться ботом для подсчета депозитов/выводов в казино уже сейчас \uD83C\uDF52");
                editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessage.setChatId(chatId);

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                inlineKeyboardButton1.setText("➕Добавить казино");
                inlineKeyboardButton1.setCallbackData("add casino");

                InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                inlineKeyboardButton2.setText("➖Удалить казино");
                inlineKeyboardButton2.setCallbackData("delete casino");

                List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
                List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
                List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
                List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();

                List<Casino> casinos = casinoService.getAllCasinoByChatId(chatId);

                int iteration = 0;

                for (Casino casino : casinos){

                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

                    if (iteration > 9){
                        inlineKeyboardButton.setText(casino.getName());
                        inlineKeyboardButton.setCallbackData(casino.getName());

                        keyboardButtonsRow4.add(inlineKeyboardButton);
                    } else if (iteration >= 5){
                        inlineKeyboardButton.setText(casino.getName());
                        inlineKeyboardButton.setCallbackData(casino.getName());

                        keyboardButtonsRow3.add(inlineKeyboardButton);
                    } else {
                        inlineKeyboardButton.setText(casino.getName());
                        inlineKeyboardButton.setCallbackData(casino.getName());

                        keyboardButtonsRow2.add(inlineKeyboardButton);
                    }

                    iteration+=1;
                }

                keyboardButtonsRow1.add(inlineKeyboardButton1);
                keyboardButtonsRow1.add(inlineKeyboardButton2);


                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(keyboardButtonsRow1);
                rowList.add(keyboardButtonsRow2);
                rowList.add(keyboardButtonsRow3);
                rowList.add(keyboardButtonsRow4);

                inlineKeyboardMarkup.setKeyboard(rowList);

                editMessage.setReplyMarkup(inlineKeyboardMarkup);

                try {
                    execute(editMessage);
                } catch (Exception ignored){
                }

                return;
            }


            List<Casino> casinos = casinoService.getAllCasinoByChatId(chatId);

            for(Casino casino : casinos) {
                if (callBackData.equals(casino.getName())){

                    EditMessageText editMessageText = new EditMessageText();

                    editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMessageText.setChatId(chatId);
                    editMessageText.setText("Выберите один из вариантов действий над - " + casino.getName());

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                    InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                    inlineKeyboardButton1.setText("➕Добавить пополнение");
                    inlineKeyboardButton1.setCallbackData("add replenishment");

                    InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                    inlineKeyboardButton2.setText("➕Добавить вывод");
                    inlineKeyboardButton2.setCallbackData("add withdrawal");

                    InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
                    inlineKeyboardButton3.setText("♾Просмотреть статистику");
                    inlineKeyboardButton3.setCallbackData("check stat");

                    InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
                    inlineKeyboardButton4.setText("⭕Назад в управление казино");
                    inlineKeyboardButton4.setCallbackData("back casino management");

                    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
                    List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
                    List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

                    keyboardButtonsRow1.add(inlineKeyboardButton1);
                    keyboardButtonsRow1.add(inlineKeyboardButton2);

                    keyboardButtonsRow2.add(inlineKeyboardButton3);

                    keyboardButtonsRow3.add(inlineKeyboardButton4);

                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(keyboardButtonsRow1);
                    rowList.add(keyboardButtonsRow2);
                    rowList.add(keyboardButtonsRow3);

                    inlineKeyboardMarkup.setKeyboard(rowList);


                    editMessageText.setReplyMarkup(inlineKeyboardMarkup);

                    try {
                        execute(editMessageText);
                    } catch (Exception ignored){
                    }
                    return;
                }
            }

        }
    }
}
