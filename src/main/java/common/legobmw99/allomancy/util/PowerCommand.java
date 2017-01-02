package common.legobmw99.allomancy.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.network.packets.AllomancyPowerPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class PowerCommand extends CommandBase {

    private final List aliases;
    protected int level;
    protected String[] names = { "Uninvested", "Iron Misting", "Steel Misting", "Tin Misting", "Pewter Misting", "Zinc Misting", "Brass Misting", "Copper Misting", "Bronze Misting", "Mistborn" };

    public PowerCommand() {
        aliases = new ArrayList();
        aliases.add("ap");
    }

    @Override
    public String getName() {
        return "allomancy_power";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/allomancy_power <number> [player] \nNumber is the player's allomancy power, 0 through 9";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        World world = sender.getEntityWorld();

        if (world.isRemote) {

        } else {
            if ((args.length == 0 || args.length > 2)) {
                throw new WrongUsageException(this.getUsage(sender), new Object[0]);
            } else {

                level = parseInt(args[0]) - 1;

                EntityPlayer entityplayer = args.length > 1 ? getPlayer(server, sender, args[1]) : getCommandSenderAsPlayer(sender);

                if (level < -1 || level > 8) {
                    sender.sendMessage(new TextComponentString("Level must be between 0 and 9"));
                    return;
                }

                if (entityplayer != null) {

                    AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(entityplayer);
                    cap.setAllomancyPower(level);

                    Registry.network.sendTo(new AllomancyPowerPacket(level), (EntityPlayerMP) entityplayer);

                    notifyCommandListener(sender, this, "Setting " + entityplayer.getName() + " to " + names[level + 1], new Object[] { Integer.valueOf(level), entityplayer.getName() });

                } else {
                    sender.sendMessage(new TextComponentString("Player not found"));
                }
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, this.getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return args.length == 2 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.<String>emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

}
