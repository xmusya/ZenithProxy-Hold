package dev.hold.module;

import com.github.rfresh2.EventConsumer;
import com.zenith.event.client.ClientBotTick;
import com.zenith.feature.player.ClickTarget;
import com.zenith.feature.player.Input;
import com.zenith.feature.player.InputRequest;
import com.zenith.module.api.Module;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.rfresh2.EventConsumer.of;
import static com.zenith.Globals.INPUTS;
import static dev.hold.HoldPlugin.PLUGIN_CONFIG;

/**
 * Keeps keys held down by re-submitting an input request on every
 * bot tick until the hold is cleared with {@code hold stop}.
 */
public class HoldModule extends Module {
    public enum Key {
        SNEAK, JUMP, LEFT, RIGHT
    }

    private final Set<Key> held = ConcurrentHashMap.newKeySet();

    @Override
    public boolean enabledSetting() {
        return true;
    }

    @Override
    public List<EventConsumer<?>> registerEvents() {
        return List.of(of(ClientBotTick.class, this::onTick));
    }

    public void hold(final Key key) {
        held.add(key);
    }

    public void stop(final Key key) {
        held.remove(key);
    }

    public void stopAll() {
        held.clear();
    }

    public boolean isHolding(final Key key) {
        return held.contains(key);
    }

    public Set<Key> activeHolds() {
        return Set.copyOf(held);
    }

    private void onTick(final ClientBotTick event) {
        if (held.isEmpty()) return;
        try {
            Input input = Input.builder()
                .sneaking(held.contains(Key.SNEAK))
                .jumping(held.contains(Key.JUMP))
                .leftClick(held.contains(Key.LEFT))
                .rightClick(held.contains(Key.RIGHT))
                .hand(Hand.MAIN_HAND)
                .clickTarget(ClickTarget.Any.INSTANCE)
                .clickRequiresRotation(false)
                .build();
            INPUTS.submit(InputRequest.builder()
                .owner(this)
                .input(input)
                .priority(PLUGIN_CONFIG.priority)
                .build());
        } catch (final Exception e) {
            error("Hold input submit failed", e);
        }
    }
}
