package com.github.tvbox.osc.util;

import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.ui.activity.SearchActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class SearchHelper {
    private static String api = Hawk.get(HawkConfig.API_URL, "");
    public static HashMap<String, String> getSourcesForSearch() {
    HashMap<String, String> mCheckSources;
        if (api.isEmpty()) {
            return null;
        }
        try {
            HashMap<String, HashMap<String, String>> mCheckSourcesForApi = Hawk.get(HawkConfig.SOURCES_FOR_SEARCH, new HashMap<>());
            mCheckSources = mCheckSourcesForApi.get(api);
            Hawk.put(HawkConfig.SOURCES_FOR_SEARCH, mCheckSourcesForApi);
        } catch (Exception e) {
            return null;
        }
        if (mCheckSources == null || mCheckSources.isEmpty()) {
            mCheckSources = getSources();
        } else {
            HashMap<String, String> newSources = getSources();
            for (Map.Entry<String, String> entry : newSources.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!mCheckSources.containsKey(key)) {
                    mCheckSources.put(key, value);
                }
            }
        }
        return mCheckSources;
    }

    public static void putCheckedSources(HashMap<String, String> mCheckSources, boolean isAll) {
        if (api.isEmpty()) {
            return;
        }
        HashMap<String, HashMap<String, String>> mCheckSourcesForApi = Hawk.get(HawkConfig.SOURCES_FOR_SEARCH, null);
        if (isAll) {
            if (mCheckSourcesForApi == null) {
                return;
            }
            if (mCheckSourcesForApi.containsKey(api)) {
                mCheckSourcesForApi.remove(api);
            }
        } else {
            if (mCheckSourcesForApi == null) {
                mCheckSourcesForApi = new HashMap<>();
            }
            mCheckSourcesForApi.put(api, mCheckSources);
        }
        SearchActivity.setCheckedSourcesForSearch(mCheckSources);
        //Hawk.put(HawkConfig.SOURCES_FOR_SEARCH, mCheckSourcesForApi);
    }

    public static HashMap<String, String> getSources() {
        HashMap<String, String> mCheckSources = new HashMap<>();
        for (SourceBean bean : ApiConfig.get().getSourceBeanList()) {
            if (!bean.isSearchable()) {
                continue;
            }
            mCheckSources.put(bean.getKey(), "1");
        }
        return mCheckSources;
    }

    public static List<String> splitWords(String text) {
        List<String> result = new ArrayList<>();
        result.add(text);
        String[] parts = text.split("\\W+");
        if (parts.length > 1) {
            result.addAll(Arrays.asList(parts));
        }
        return result;
    }
}
