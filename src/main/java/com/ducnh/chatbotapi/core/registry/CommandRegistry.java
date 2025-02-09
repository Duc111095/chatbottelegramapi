package com.ducnh.chatbotapi.core.registry;

import com.ducnh.chatbotapi.exception.BotException;
import com.ducnh.chatbotapi.model.BotCommand;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class CommandRegistry {
    private final Map<String, BotCommand> botCommandMap = new ConcurrentHashMap<>();

    public int getSize() {
        return this.botCommandMap.keySet().size();
    }

    public Set<String> getCommandNames(){
        return this.botCommandMap.keySet();
    }

    public void register(BotCommand botCommand){
        if (hasCommand(botCommand.getCmd())) {
            throw new BotException("There is " + botCommand.getCmd() + " exist on CommandRegistry, please check your command");
        }
        this.botCommandMap.put(botCommand.getCmd(), botCommand);
    }

    public Collection<BotCommand> getCommands(){
        return this.botCommandMap.values();
    }

    public boolean hasCommand(String cmd){
        return this.botCommandMap.containsKey(cmd);
    }

    public BotCommand getCommand(String cmd){
        return this.botCommandMap.get(cmd);
    }
}
