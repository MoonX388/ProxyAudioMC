package com.moonx;

import org.bukkit.plugin.Plugin;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class ProxyAudioMC {

    private ProtocolManager protocolManager;
    private PacketAdapter chatListener;

    public void enable(Plugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();

        chatListener = new PacketAdapter(plugin, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                // Logic intercept packet di sini
            }
        };

        protocolManager.addPacketListener(chatListener);
    }

    public void disable() {
        if (protocolManager != null && chatListener != null) {
            protocolManager.removePacketListener(chatListener);
        }
    }
}
