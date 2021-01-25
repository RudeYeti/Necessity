package io.github.rudeyeti.necessity.modules.schematics;

import github.scarsz.discordsrv.dependencies.commons.io.FilenameUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.okhttp3.OkHttpClient;
import github.scarsz.discordsrv.dependencies.okhttp3.Request;
import github.scarsz.discordsrv.dependencies.okhttp3.Response;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class File {
    private static final List<String> getMessage = new ArrayList<String>() {{
        add("Usage: The specified file `%s` must be a schematic.");
        add("Usage: The schematic `%s` exceeds the file size limit of `%s KB`.");
        add("The schematic `%s` has been successfully uploaded.");
    }};

    private static void getErrorMessage(boolean isCommand, TextChannel channel, Message message, String errorMessage) {
        if (isCommand) {
            channel.sendMessage(errorMessage).queue();
        } else {
            channel.sendMessage(errorMessage).complete().delete().completeAfter(3, TimeUnit.SECONDS);
            message.delete().queue();
        }
    }

    protected static String download(URL url, java.io.File destFolder) {
        try {
            Request request = new Request.Builder().url(url).header("User-Agent", "Necessity").build();
            Response response = new OkHttpClient().newBuilder().build().newCall(request).execute();
            InputStream inputStream = response.body().byteStream();

            String fileName = response.headers().names().contains("content-disposition") ?
                    response.headers().get("content-disposition").replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1") :
                    new java.io.File(url.getPath()).getName();

            java.io.File file = new java.io.File(destFolder, fileName);

            if (fileName.endsWith(".schematic")) {
                for (int i = 1; file.exists(); i++) {
                    file = new java.io.File(destFolder, FilenameUtils.removeExtension(fileName) + i + ".schematic");
                }

                fileName = file.getName();

                ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int fileSize = (int) response.body().contentLength();

                if (fileSize > Integer.parseInt(Config.get.sizeLimit) * 1000) {
                    inputStream.close();
                    readableByteChannel.close();
                    fileOutputStream.close();
                    file.delete();

                    return String.format(getMessage.get(1), fileName, Config.get.sizeLimit);
                }

                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, fileSize);
                inputStream.close();
                readableByteChannel.close();
                fileOutputStream.close();

                return String.format(getMessage.get(2), fileName);
            } else {
                return String.format(getMessage.get(0), fileName);
            }
        } catch (IOException error) {
            error.printStackTrace();
            return "Usage: An unknown error occurred when attempting to download the file.";
        }
    }

    protected static void get(GuildMessageReceivedEvent event) {
        if (!Config.get.commandMode && event.getChannel().getId().equals(Config.get.schematicsChannelId)) {
            get(false, event.getGuild(), event.getChannel(), event.getMessage(), event.getMessage().getContentRaw());
        }
    }

    protected static void get(boolean isCommand, Guild guild, TextChannel channel, Message message, String urlString) {
        if (guild == Necessity.guild) {
            java.io.File schematicsFolder = new java.io.File(Plugins.getWorldEdit().getDataFolder() + java.io.File.separator + "schematics");

            try {
                if (!schematicsFolder.exists()) {
                    schematicsFolder.mkdir();
                }

                // If the message is not a url, an exception will be thrown.
                URL url = new URL(urlString);
                String downloadFile = download(url, schematicsFolder);

                if (downloadFile.startsWith("Usage:")) {
                    getErrorMessage(isCommand, channel, message, downloadFile);
                } else {
                    channel.sendMessage(downloadFile).queue();
                }
            } catch (MalformedURLException error) {
                List<Message.Attachment> attachments = message.getAttachments();

                // Otherwise download an attachment if it exists.
                if (attachments.size() == 1) {
                    String fileName = attachments.get(0).getFileName();

                    if (fileName.endsWith(".schematic")) {
                        java.io.File file = new java.io.File(schematicsFolder, fileName);

                        for (int i = 1; file.exists(); i++) {
                            file = new java.io.File(schematicsFolder, FilenameUtils.removeExtension(fileName) + i + ".schematic");
                        }

                        fileName = file.getName();

                        if (!(attachments.get(0).getSize() > Integer.parseInt(Config.get.sizeLimit) * 1000)) {
                            attachments.get(0).downloadToFile(file);
                            channel.sendMessage(String.format(getMessage.get(2), fileName)).queue();
                        } else {
                            getErrorMessage(isCommand, channel, message, String.format(getMessage.get(1), fileName, Config.get.sizeLimit));
                        }
                    } else {
                        getErrorMessage(isCommand, channel, message, String.format(getMessage.get(0), fileName));
                    }
                } else {
                    getErrorMessage(isCommand, channel, message, "Usage: The message must either contain a link or have a schematic attached to it.");
                }
            }
        }
    }
}
