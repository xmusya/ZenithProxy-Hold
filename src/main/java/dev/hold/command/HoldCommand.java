package dev.hold.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
import dev.hold.module.HoldModule;

import static com.zenith.Globals.MODULE;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;

public class HoldCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("hold")
            .category(CommandCategory.MODULE)
            .description("""
                Holds a key down until you stop it.

                sneak - holds sneak (shift)
                jump  - holds jump
                left  - holds left click (attack/break)
                right - holds right click (use/place)
                stop  - releases everything
                """)
            .usageLines(
                "sneak",
                "jump",
                "left",
                "right",
                "stop",
                "<sneak|jump|left|right> off"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("hold").requires(Command::validateAccountOwner)
            .then(literal("stop").executes(c -> {
                HoldModule module = MODULE.get(HoldModule.class);
                String before = keysStr(module);
                module.stopAll();
                c.getSource().getEmbed()
                    .title("Hold Stopped")
                    .description(before.equals("(none)")
                        ? "Nothing was held"
                        : "Released: " + before);
            }))
            .then(keyLiteral("sneak", HoldModule.Key.SNEAK))
            .then(keyLiteral("jump", HoldModule.Key.JUMP))
            .then(keyLiteral("left", HoldModule.Key.LEFT))
            .then(keyLiteral("right", HoldModule.Key.RIGHT));
    }

    private LiteralArgumentBuilder<CommandContext> keyLiteral(final String name, final HoldModule.Key key) {
        return literal(name)
            .executes(c -> {
                setKey(c, key, true);
            })
            .then(argument("toggle", toggle()).executes(c -> {
                setKey(c, key, getToggle(c, "toggle"));
            }));
    }

    private void setKey(final com.mojang.brigadier.context.CommandContext<CommandContext> c,
                        final HoldModule.Key key,
                        final boolean on) {
        HoldModule module = MODULE.get(HoldModule.class);
        if (on) {
            module.hold(key);
            c.getSource().getEmbed()
                .title("Holding " + key.name().toLowerCase())
                .description("Release with: hold stop");
        } else {
            module.stop(key);
            c.getSource().getEmbed()
                .title("Released " + key.name().toLowerCase())
                .description("Currently holding: " + keysStr(module));
        }
    }

    @Override
    public void defaultEmbed(final Embed embed) {
        HoldModule module = MODULE.get(HoldModule.class);
        embed
            .title("Hold Status")
            .addField("Holding", keysStr(module))
            .primaryColor();
    }

    private static String keysStr(final HoldModule module) {
        var active = module.activeHolds();
        if (active.isEmpty()) return "(none)";
        StringBuilder sb = new StringBuilder();
        for (HoldModule.Key key : active) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(key.name().toLowerCase());
        }
        return sb.toString();
    }
}
