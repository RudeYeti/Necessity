package io.github.rudeyeti.necessity.utils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Schematic {
    public static String message(int id, String fileName) {
        switch (id) {
            case 0:
                return "Usage: The specified file `" + fileName + "`must be a schematic.";
            case 1:
                return "Usage: The schematic `" + fileName + "` already exists.";
            case 2:
                return "Usage: The schematic `" + fileName + "` exceeds the file size limit of `" + Config.sizeLimit / 1000 + " MB`.";
            case 3:
                return "The schematic `" + fileName + "` has been successfully uploaded.";
        }
        return null;
    }

    public static void errorMessage(GuildMessageReceivedEvent event, String errorMessage) {
        event.getChannel().sendMessage(errorMessage).complete().delete().completeAfter(3, TimeUnit.SECONDS);
        event.getMessage().delete().queue();
    }

    public static String download(URL url, File destFolder) {
        String fileName = new File(url.getPath()).getName();
        File file = new File(destFolder, fileName);

        if (fileName.endsWith(".schematic")) {
            if (!file.exists()) {
                try {
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty("User-Agent", "SchematicsPlus");
                    connection.connect();

                    ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int fileSize = IOUtils.toByteArray(connection.getInputStream()).length;

                    if (fileSize > Config.sizeLimit) {
                        readableByteChannel.close();
                        fileOutputStream.close();
                        file.delete();

                        return message(2, fileName);
                    }

                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    readableByteChannel.close();
                    fileOutputStream.close();

                    return message(3, fileName);
                } catch (IOException error) {
                    error.printStackTrace();
                }
            } else {
                return message(1, fileName);
            }
        } else {
            return message(0, fileName);
        }
        return "Usage: An unknown error occurred when attempting to download the file `" + fileName + "`.";
    }

    public static void get(GuildMessageReceivedEvent event) {
        if (event.getGuild() == Necessity.guild && event.getChannel().getId().equals(Config.schematicsChannelId) && !event.getAuthor().isBot()) {
            File schematicsFolder = new File(Plugins.getWorldEdit().getDataFolder() + File.separator + "schematics");

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
                        File file = new File(schematicsFolder, fileName);

                        if (!file.exists()) {
                            if (!(attachments.get(0).getSize() > Config.sizeLimit)) {
                                attachments.get(0).downloadToFile(file);
                                event.getChannel().sendMessage(message(3, fileName)).queue();
                            } else {
                                errorMessage(event, Schematic.message(2, fileName));
                            }
                        } else {
                            errorMessage(event, Schematic.message(1, fileName));
                        }
                    } else {
                        errorMessage(event, Schematic.message(0, fileName));
                    }
                } else {
                    errorMessage(event, "Usage: The message must either contain a link or have a schematic attached to it.");
                }
            }
        }
    }
}
