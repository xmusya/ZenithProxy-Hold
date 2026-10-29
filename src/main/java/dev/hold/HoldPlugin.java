package dev.hold;

import com.zenith.plugin.api.Plugin;
import com.zenith.plugin.api.PluginAPI;
import com.zenith.plugin.api.ZenithProxyPlugin;
import dev.hold.command.HoldCommand;
import dev.hold.module.HoldModule;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

@Plugin(
    id = BuildConstants.PLUGIN_ID,
    version = BuildConstants.VERSION,
    description = "Hold sneak/jump/left/right until stopped",
    url = "https://github.com/xmusya/ZenithProxy-Hold",
    authors = {"xmusya"},
    mcVersions = {"*"}
)
public class HoldPlugin implements ZenithProxyPlugin {
    public static HoldConfig PLUGIN_CONFIG;
    public static ComponentLogger LOG;

    @Override
    public void onLoad(PluginAPI pluginAPI) {
        LOG = pluginAPI.getLogger();
        PLUGIN_CONFIG = pluginAPI.registerConfig(BuildConstants.PLUGIN_ID, HoldConfig.class);
        pluginAPI.registerModule(new HoldModule());
        pluginAPI.registerCommand(new HoldCommand());
    }
}
