package org.eu.hanana.reimu.chatimage.command;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.eu.hanana.reimu.chatimage.core.ChatImage;
import org.eu.hanana.reimu.mc.lcr.command.CommandBase;

import java.util.List;

public class ChatImageCommand extends CommandBase {
    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommand() {
        return "chatimage";
    }

    @Override
    public int execute(CommandSourceStack commandSourceStack, String rawCommand) throws Exception {
        var args = this.parseCommand(rawCommand);
        if (args.length>1){
            if (args[1].equals("cache")){
                if (args[2].equals("list")){
                    var i =0;
                    var sb = Component.literal(ChatImage.BufferedChatImage.size()+" item(s) in the server cache.\n");
                    for (String s : ChatImage.BufferedChatImage.keySet()) {
                        sb.append(String.valueOf(i)).append("• ").append(Component.literal(s).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent.SuggestCommand(s)).withHoverEvent(new Actions.ShowImage(Component.literal(s))))).append("\n");
                        i++;
                    }
                    commandSourceStack.sendSuccess(()-> sb,true);
                    return i;
                }else if (args[2].equals("clear")){
                    ChatImage.clearCache();
                    sendSuccess(Component.literal("Cleared server cache."),commandSourceStack);
                    return 0;
                }
            }else if (args[1].equals("send_raw")){
                List<ServerPlayer> playerBySelector = getPlayerBySelector(args[2], commandSourceStack);
                for (ServerPlayer serverPlayer : playerBySelector) {
                    ServerChatEvent serverChatEvent = new ServerChatEvent(serverPlayer, args[3], Component.literal(args[3]));
                    Util.genCIMsg(serverChatEvent);
                    serverPlayer.sendSystemMessage(serverChatEvent.getMessage());
                }
                return playerBySelector.size();
            }else if (args[1].equals("send")){
                List<ServerPlayer> playerBySelector = getPlayerBySelector(args[2], commandSourceStack);
                String ciCode = new ChatImage.ChatImageData(args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5]), args[6]).toCiCode();
                for (ServerPlayer serverPlayer : playerBySelector) {
                    ServerChatEvent serverChatEvent = new ServerChatEvent(serverPlayer, ciCode, Component.literal(ciCode));
                    Util.genCIMsg(serverChatEvent);
                    serverPlayer.sendSystemMessage(serverChatEvent.getMessage());
                }
                return playerBySelector.size();
            }else if (args[1].equals("test")){
                List<ServerPlayer> playerBySelector = commandSourceStack.getServer().getPlayerList().getPlayers();
                var ciCode = "CI{\"url\":\"ci:cp/assets/chatimage/test_logo.png\",\"w\":111,\"h\":111,\"info\":\"THIS IS A TEST IMAGE.\"}";
                for (ServerPlayer serverPlayer : playerBySelector) {
                    ServerChatEvent serverChatEvent = new ServerChatEvent(serverPlayer, ciCode, Component.literal(ciCode));
                    Util.genCIMsg(serverChatEvent);
                    serverPlayer.sendSystemMessage(serverChatEvent.getMessage());
                }
                sendSuccess(Component.literal("Test image has been sent to all players."),commandSourceStack);
                return playerBySelector.size();
            }
        }else {
            throw new Exception("Incomplete command!");
        }
        return 0;
    }
    private void sendSuccess(Component component , CommandSourceStack sourceStack){
        sourceStack.sendSuccess(()-> component,true);
    }
    @Override
    public String getSuggestion(String string, Player player) {
        var args = this.parseCommand(string);
        if (args.length==1){
            return string+" ";
        }
        if (args.length==2){
            return cycleTabSuggestion(player,args,new String[]{"cache","send_raw","send","test"},true);
        }
        if (args.length==3){
            if (args[1].equals("cache")){
                return cycleTabSuggestion(player,args,new String[]{"list","clear"},true);
            } else if (args[1].equals("send_raw")||args[1].equals("send")){
                return cycleTabSuggestion(player,args,selectorSuggestions(),true);
            }
        }
        return super.getSuggestion(string, player);
    }
}
