package com.github.duc010298.android.helper;

import android.content.Context;
import android.content.res.Resources;

import com.github.duc010298.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigHelper {
    public static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
