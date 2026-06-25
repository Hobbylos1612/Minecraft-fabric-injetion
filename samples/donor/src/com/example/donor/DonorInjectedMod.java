package com.example.donor;

import net.fabricmc.api.ModInitializer;

public final class DonorInjectedMod implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("[donor-injected] Injected class started with Minecraft/Fabric.");
    }
}
