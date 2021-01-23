package io.github.rudeyeti.necessity.modules.schematics;

import github.scarsz.discordsrv.dependencies.commons.io.FilenameUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class File {
    protected static List<List<String>> message = new ArrayList<List<String>>() {{
        add(Arrays.asList("Usage: The specified file `", "` must be a schematic."));
        add(Arrays.asList("Usage: The schematic `", "` exceeds the file size limit of `", " KB`."));
        add(Arrays.asList("The schematic `", "` has been successfully uploaded."));
    }};

    protected static void errorMessage(GuildMessageReceivedEvent event, String errorMessage) {
        event.getChannel().sendMessage(errorMessage).complete().delete().completeAfter(3, TimeUnit.SECONDS);
        event.getMessage().delete().queue();
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

                    return message.get(1).get(0) + fileName + message.get(1).get(1) + Config.get.sizeLimit + message.get(1).get(2);
                }

                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, fileSize);
                inputStream.close();
                readableByteChannel.close();
                fileOutputStream.close();

                return message.get(2).get(0) + fileName + message.get(2).get(1);
            } else {
                return message.get(0).get(0) + fileName + message.get(0).get(1);
            }
        } catch (IOException error) {
            error.printStackTrace();
            return "Usage: An unknown error occurred when attempting to download the file.";
        }
    }

    protected static void get(GuildMessageReceivedEvent event) {
        if (event.getGuild() == Necessity.guild && event.getChannel().getId().equals(Config.get.schematicsChannelId)) {
            java.io.File schematicsFolder = new java.io.File(Plugins.getWorldEdit().getDataFolder() + java.io.File.separator + "schematics");

            try {
                if (!schematicsFolder.exists()) {
                    schematicsFolder.mkdir();
                }

                // If the message is not a url, an exception will be thrown.
                URL url = new URL(event.getMessage().getContentRaw());
                String downloadFile = download(url, schematicsFolder);

                if (!downloadFile.startsWith("Usage:")) {
                    event.getChannel().sendMessage(downloadFile).queue();
                } else {
                    errorMessage(event, downloadFile);
                }
            } catch (MalformedURLException error) {
                List<Message.Attachment> attachments = event.getMessage().getAttachments();

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
                            event.getChannel().sendMessage(message.get(2).get(0) + fileName + message.get(2).get(1)).queue();
                        } else {
                            errorMessage(event, message.get(1).get(0) + fileName + message.get(1).get(1) + Config.get.sizeLimit + message.get(1).get(2));
                        }
                    } else {
                        errorMessage(event, message.get(0).get(0) + fileName + message.get(0).get(1));
                    }
                } else {
                    errorMessage(event, "Usage: The message must either contain a link or have a schematic attached to it.");
                }
            }
        }
    }
}
