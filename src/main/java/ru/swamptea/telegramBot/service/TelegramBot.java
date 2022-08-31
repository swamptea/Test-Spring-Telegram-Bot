package ru.swamptea.telegramBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.swamptea.telegramBot.config.BotConfig;


import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.apache.commons.io.FileUtils.getFile;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config){
        this.config = config;
    }

    public String getBotUsername() {
        return config.getBotName();
    }

    public String getBotToken() {
        return config.getBotToken();
    }

    public void onUpdateReceived(Update update) {


        /*// метод для скачивания картинки, присланной пользователем
        if(update.hasMessage() && update.getMessage().hasPhoto()) {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            String f_id = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null).getFileId();
            GetFile getFile = new GetFile();
            getFile.setFileId(f_id);
            try {
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                downloadFile(file, new File("newfile.jpg"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }*/


        //метод для отправления пользователю присланной картинки обратно
        if(update.hasMessage() && update.getMessage().hasPhoto()){
            long chatId = update.getMessage().getChatId();
            List<PhotoSize> photos = update.getMessage().getPhoto();
            String f_id = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null).getFileId();
            File file = getFile(f_id);
            String  path = file.getPath();
            sendPhoto(chatId, path, "");
        }

        /*int f_width = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null).getWidth();
        int f_height = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null)).getHeight();
        String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight: " + Integer.toString(f_height);*/


        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "да":
                case "Да":
                    sendMessage(chatId, ":)");
                    break;
                case "котик":
                case "Котик":
                    String[] catPhotos = {"https://thumbs.dreamstime.com/z/grey-funny-cat-posing-pink-background-92409397.jpg",
                    "https://thumbs.dreamstime.com/z/%D1%81%D0%BF%D0%B0%D1%82%D1%8C-%D0%BA%D0%BE%D1%82%D0%B0-1731354.jpg",
                    "https://thumbs.dreamstime.com/z/tabby-%D1%85%D0%BE%D0%B4%D0%B0-%D1%81%D1%83%D0%B4%D0%B0-%D0%BA%D0%BE%D1%82%D0%B0-%D0%BF%D0%BE%D0%BB%D1%8C%D0%BD%D0%BE%D1%81%D1%82%D1%8C%D1%8E-%D0%BF%D0%BE%D0%BC%D0%B5%D1%80%D0%B0%D0%BD%D1%86%D0%BE%D0%B2%D1%8B%D0%B9-24082945.jpg",
                    "https://thumbs.dreamstime.com/z/%D0%BA%D0%BE%D1%82-%D0%BC%D0%B8%D0%BB%D1%8B%D0%B9-%D0%BD%D0%B0%D1%81%D0%BB%D0%B0%D0%B6%D0%B4%D0%B0%D1%8E%D1%89%D1%81%D1%8F-%D0%B5%D0%B3%D0%BE-%D0%B6%D0%B8%D0%B7%D0%BD%D1%8C%D1%8E-15306012.jpg",
                    "https://thumbs.dreamstime.com/z/%D0%BA%D0%BE%D1%82-%D0%B3%D0%BE%D1%82%D0%BE%D0%B2%D1%8B%D0%B9-%D1%8F-%D1%82%D0%BE%D0%B3%D0%BE-%D1%87%D1%82%D0%BE%D0%B1%D1%8B-%D0%B0%D1%82%D0%B0%D0%BA%D0%BE%D0%B2%D0%B0%D1%82%D1%8C-91003403.jpg"};
                    Random random = new Random();
                    int r = random.nextInt(catPhotos.length);
                    try {
                        URL url = new URL(catPhotos[r]);
                        InputStream in = url.openStream();
                        String path = "someFile.jpg";
                        Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                        in.close();
                        sendPhoto(chatId, path, "Meow! ^_^");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    sendMessage(chatId, "Sorry, I can't do it :(");
            }
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answer = "Hi, " + name + ", I'm the Sasha's little bot!";
        log.info("Replied to user " + name);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try{
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private  void sendPhoto(long chatId, String path, String caption){
        SendPhoto msg = new SendPhoto();
        InputFile inputFile = new InputFile(path);
        msg.setPhoto(inputFile);
        msg.setCaption(caption);
        msg.setChatId(String.valueOf(chatId));
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
