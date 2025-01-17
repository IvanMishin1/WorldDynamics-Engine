package com.thatguycy.worlddynamicsengine;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;
import net.milkbowl.vault.economy.Economy;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;

public class OrganizationManager {

    private Map<String, OrganizationProperties> organizations;
    private File organizationsFile;
    private FileConfiguration organizationsConfig;

    public OrganizationManager(File dataFolder) {
        this.organizations = new HashMap<>();
        this.organizationsFile = new File(dataFolder, "organizations.yml");
        this.organizationsConfig = YamlConfiguration.loadConfiguration(organizationsFile);
        loadOrganizations();
    }

    private void loadOrganizations() {
        if (organizationsConfig.isConfigurationSection("organizations")) {
            for (String orgName : organizationsConfig.getConfigurationSection("organizations").getKeys(false)) {
                String leader = organizationsConfig.getString("organizations." + orgName + ".leader");
                Set<String> members = new HashSet<>(organizationsConfig.getStringList("organizations." + orgName + ".members"));
                OrganizationProperties.OrganizationType type = OrganizationProperties.OrganizationType.valueOf(
                        organizationsConfig.getString("organizations." + orgName + ".type"));

                OrganizationProperties orgProps = new OrganizationProperties(orgName, leader, type);
                orgProps.getMembers().addAll(members);
                organizations.put(orgName, orgProps);
                double balance = organizationsConfig.getDouble("organizations." + orgName + ".balance", 0.0);
                orgProps.setBalance(balance);
            }
        }
    }

    public void saveOrganizations() {
        for (Map.Entry<String, OrganizationProperties> entry : organizations.entrySet()) {
            String orgName = entry.getKey();
            OrganizationProperties orgProps = entry.getValue();

            organizationsConfig.set("organizations." + orgName + ".leader", orgProps.getLeader());
            organizationsConfig.set("organizations." + orgName + ".members", new ArrayList<>(orgProps.getMembers()));
            organizationsConfig.set("organizations." + orgName + ".type", orgProps.getType().name());
            organizationsConfig.set("organizations." + entry.getKey() + ".balance", entry.getValue().getBalance());

        }

        try {
            organizationsConfig.save(organizationsFile);
        } catch (IOException e) {
            e.printStackTrace(); // Handle this appropriately
        }
    }

    public OrganizationManager() {
        this.organizations = new HashMap<>();
    }

    public void addOrganization(String name, OrganizationProperties organization) {
        organizations.put(name, organization);
    }

    public void removeOrganization(String name) {
        organizations.remove(name);
    }

    public OrganizationProperties getOrganization(String name) {
        return organizations.get(name);
    }

    public Map<String, OrganizationProperties> getOrganizations() {
        return new HashMap<>(organizations);
    }

    // Additional methods for saving and loading organization data
}
