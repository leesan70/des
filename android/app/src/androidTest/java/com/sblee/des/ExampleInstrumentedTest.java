package com.des;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.des.objects.Building;
import com.des.objects.User;
import com.des.util.UserPref;
import com.des.util.Utils;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.des", appContext.getPackageName());
    }

    @Test
    public void loadBuildingText() throws Exception {
        final Context appContext = InstrumentationRegistry.getTargetContext();

        final Location location = new Location("");
        location.setLatitude(43.6631913);
        location.setLongitude(-79.3847434);

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable buildingLoader = new Runnable() {
            @Override
            public void run() {
                //Code that uses AsyncHttpClient in your case ConsultaCaract()
                Building.getBuildingsNear(location, appContext, new Building.OnBuildingLoadedListener() {
                    @Override
                    public void onLoaded(List<Building> buildings) {
                        assertEquals(buildings.size(), 1);
                    }
                });
            }
        };
        mainHandler.post(buildingLoader);
    }

    @Test
    public void userLoggedInCheck() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        UserPref pref = new UserPref(appContext);
        assertTrue(pref.isUserLoggedIn() == !pref.getFacebookId().equals(""));
    }

    @Test
    public void createUserTest() throws Exception {
        JSONObject data = new JSONObject();
        data.put("name", "TEST");
        User user = new User(data);
        assertEquals(user.getUsername(), "TEST");
    }

    @Test
    public void createBuildingTest() throws Exception {
        JSONObject data = new JSONObject();
        data.put("building_id", 1);
        data.put("building_name", "ba");
        data.put("building_address", "123");
        Building building = new Building(data);
        assertEquals(building.getId(), 1);
        assertEquals(building.getName(), "ba");
        assertEquals(building.getAddress(), "123");
        assertEquals(building.getProfileImage(),
                Utils.ADDRESS + String.format("images/%s.jpg", "ba"));
    }
}
