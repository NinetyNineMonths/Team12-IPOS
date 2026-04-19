package main.service;

import main.model.Campaign;


import java.util.ArrayList;
import java.util.List;

/**
 * In-memory store for campaign objects used by the IPOS-PU subsystem.
 *
 * This class provides simple access methods for adding, retrieving,
 * removing, and refreshing campaign data during application runtime.
 */

public class CampaignStore {

    private static final List<Campaign> campaigns = new ArrayList<>();

    /**
     * Adds a campaign to the store if it is not null
     * and does not already exist.
     */
    public static void addCampaign(Campaign campaign) {
        if (campaign != null) {
            Campaign existing = findById(campaign.getCampaignId());
            if (existing == null) {
                campaigns.add(campaign);
            }
        }
    }

    /**
     * Returns all campaigns currently stored in memory.
     */
    public static List<Campaign> getAllCampaigns() {
        return new ArrayList<>(campaigns);
    }

    /**
     * Returns only campaigns that are currently active.
     */
    public static List<Campaign> getActiveCampaigns() {
        List<Campaign> active = new ArrayList<>();
        for (Campaign c : campaigns) {
            if (c.isActive()) {
                active.add(c);
            }
        }
        return active;
    }

    /**
     * Searches for a campaign by its campaign ID.
     */
    public static Campaign findById(String campaignId) {
        for (Campaign c : campaigns) {
            if (c.getCampaignId().equalsIgnoreCase(campaignId)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Removes a campaign from the store using its campaign ID.
     */
    public static boolean removeCampaign(String campaignId) {
        return campaigns.removeIf(c -> c.getCampaignId().equalsIgnoreCase(campaignId));
    }

    /**
     * Clears all campaigns currently stored in memory.
     */
    public static void clear() {
        campaigns.clear();
    }

    /**
     * Reloads campaign data from the database using the promotion service.
     */
    public static void loadFromDatabase(PromotionService promotionService) {
        campaigns.clear();
        campaigns.addAll(promotionService.getAllCampaigns());
    }
}
