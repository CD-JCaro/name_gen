package dnd.namegen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    Random rand = new Random();
    boolean bChange = false;
    Map<String, Map<String, Map<String, List<String>>>> mNames = new HashMap<String, Map<String, Map<String, List<String>>>>();

    String strCurrNation;
    String strCurrRace;
    String strCurrGender;

    Spinner sNations;
    Spinner sRace;
    Spinner sGender;

    Map<String, Integer> mNationImages = new HashMap<String, Integer>();
    Map<String, Integer> mRaceImages = new HashMap<String, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        prepSpinners();
        loadFile();
        populateRaces();
    }

    private void init()
    {


        sNations = (Spinner) findViewById(R.id.country_dropdown);
        sRace = (Spinner) findViewById(R.id.race_dropdown);
        sGender = (Spinner) findViewById(R.id.gender_dropdown);

        mNationImages.put("Westeros", R.drawable.westeros);
        mNationImages.put("Ivalice", R.drawable.ivalice);
        mNationImages.put("Arabia", R.drawable.arabia);
        mNationImages.put("Jin", R.drawable.jin);
        mNationImages.put("None", R.drawable.none);
        mNationImages.put("Noble", R.drawable.noble);

        mRaceImages.put("Hume", R.drawable.hume);
        mRaceImages.put("Elf", R.drawable.elf);
        mRaceImages.put("Orc", R.drawable.orc);
        mRaceImages.put("Dragonborn", R.drawable.dragonborn);
    }

    public void generateName(View view)
    {
        List<String> names = mNames.get(strCurrRace).get(strCurrNation).get(strCurrGender);

        Log.d("DEBUG", Integer.toString(names.size()));
        String name = names.get(rand.nextInt(names.size()));
        Log.d("DEBUG", name);


        TextView resultView = (TextView) findViewById(R.id.nameResult);
        resultView.setText(name);
    }

    private void prepSpinners()
    {
        sNations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                List<String> nations = new ArrayList<String>(mNames.get(strCurrRace).keySet());

                //ArrayAdapter adapter = ArrayAdapter.createFromResource(MainActivity.this, nations, R.layout.spinner_size);

                strCurrNation = nations.get(position);

                Log.d("DEBUG", strCurrNation);

                populateGenders();

                try
                {
                    ImageView nationImg = (ImageView) findViewById(R.id.nationImg);
                    nationImg.setImageResource(mNationImages.get(strCurrNation));
                }
                catch(Exception e)
                {
                    Log.d("DEBUG", "Unable to find image for" + strCurrNation);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });

        sRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                List<String> nations = new ArrayList<String>(mNames.keySet());

                strCurrRace = nations.get(position);

                Log.d("DEBUG", strCurrRace);

                populateNations();

                try
                {
                    ImageView raceImg = (ImageView) findViewById(R.id.raceImg);
                    raceImg.setImageResource(mRaceImages.get(strCurrRace));
                }
                catch(Exception e)
                {
                    Log.d("DEBUG", "Unable to find image for" + strCurrNation);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });
        sGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                List<String> genders = new ArrayList<String>(mNames.get(strCurrRace).get(strCurrNation).keySet());

                strCurrGender = genders.get(position);

                Log.d("DEBUG", strCurrGender);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });

    }

    private void populateGenders()
    {
        List<String> genders = new ArrayList<String>(mNames.get(strCurrRace).get(strCurrNation).keySet());

        strCurrGender = genders.get(0);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.selected, genders);
        adapter.setDropDownViewResource(R.layout.dropdownitem);
        sGender.setAdapter(adapter);
    }

    private void populateRaces()
    {
        List<String> races = new ArrayList<String>(mNames.keySet());

        strCurrRace = races.get(0);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.selected, races);
        adapter.setDropDownViewResource(R.layout.dropdownitem);
        sRace.setAdapter(adapter);
    }

    private void populateNations()
    {
        List<String> nations = new ArrayList<String>(mNames.get(strCurrRace).keySet());

        strCurrNation = nations.get(0);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.selected, nations);
        adapter.setDropDownViewResource(R.layout.dropdownitem);
        sNations.setAdapter(adapter);
    }

    public void loadFile()
    {
        BufferedReader reader;
        String currentRace = "";
        String currentGender = "";
        String currentNation = "";
        List<String> currentNames = new ArrayList<>();
        /* currentNames.add */
        Map<String, List<String>> raceNames = resetRaceNames();

        try
        {
            reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("Names.txt")));
            String line = reader.readLine();

            String[] current = getCurrentInfo(line);
            currentRace = current[0];
            currentGender = current[1];
            currentNation = current[2];
            mNames.put(currentRace, new HashMap<String, Map<String, List<String>>>());

            line = reader.readLine();

            while(line != null)
            {
                if(bChange)
                {
                    current = getCurrentInfo(line);
                    String race = current[0];
                    currentGender = current[1];
                    String nation = current[2];

                    if(mNames.get(currentRace) == null)
                    {
                        mNames.put(currentRace, new HashMap<String, Map<String, List<String>>>());
                    }
                    if(!race.equalsIgnoreCase(currentRace) || !nation.equalsIgnoreCase(currentNation))
                    {
                        //Log.d("DEBUG", Integer.toString(raceNames.get(currentGender).size()));
                        mNames.get(currentRace).put(currentNation, raceNames);

                        currentRace = race;
                        currentNation = nation;

                        Map<String, Map<String, List<String>>> mRaceMap = mNames.get(currentRace);
                        if(mRaceMap != null)
                        {
                            raceNames = mRaceMap.get(currentNation);

                            if (raceNames == null)
                            {
                                raceNames = resetRaceNames();
                            }
                        }
                        else
                        {
                            raceNames = resetRaceNames();
                        }
                    }
                    bChange = false;
                }
                else
                {
                    if(line.contains("___"))
                    {
                        bChange = true;
                        for(int i = 0; i < currentNames.size(); i++)
                        {
                            Log.d("DEBUG", currentNames.get(i));
                        }
                        raceNames.put(currentGender, currentNames);
                        currentNames = new ArrayList<>();
                    }
                    else if (line.trim().length() > 0)
                    {
                        currentNames.add(line);
                    }
                }
                line = reader.readLine();
            }

            if(mNames.get(currentRace) == null)
            {
                mNames.put(currentRace, new HashMap<String, Map<String, List<String>>>());
            }
            raceNames.put(currentGender, currentNames);
            mNames.get(currentRace).put(currentNation, raceNames);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.d("DEBUG", "Failed to read");
        }
        Log.d("DEBUG", "Oh man we finished...");

    }

    /*
    while(line != null)
            {
                if(bChange)
                {
                    current = getCurrentInfo(line);
                    String race = current[0];
                    currentGender = current[1];
                    String nation = current[2];

                    if(mNames.get(currentNation) == null)
                    {
                        mNames.put(currentNation, new HashMap<String, Map<String, List<String>>>());
                    }
                    if(!race.equalsIgnoreCase(currentRace) || !nation.equalsIgnoreCase(currentNation))
                    {
                        //Log.d("DEBUG", Integer.toString(raceNames.get(currentGender).size()));
                        mNames.get(currentNation).put(currentRace, raceNames);

                        currentRace = race;
                        currentNation = nation;

                        Map<String, Map<String, List<String>>> mNationMap = mNames.get(currentNation);
                        if(mNationMap != null)
                        {
                            raceNames = mNationMap.get(currentRace);

                            if (raceNames == null)
                            {
                                raceNames = resetRaceNames();
                            }
                        }
                        else
                        {
                            raceNames = resetRaceNames();
                        }
                    }
                    bChange = false;
                }
                else
                {
                    if(line.contains("___"))
                    {
                        bChange = true;
                        for(int i = 0; i < currentNames.size(); i++)
                        {
                            Log.d("DEBUG", currentNames.get(i));
                        }
                        raceNames.put(currentGender, currentNames);
                        currentNames = new ArrayList<>();
                    }
                    else if (line.trim().length() > 0)
                    {
                        currentNames.add(line);
                    }
                }
                line = reader.readLine();
            }

            if(mNames.get(currentNation) == null)
            {
                mNames.put(currentNation, new HashMap<String, Map<String, List<String>>>());
            }
            raceNames.put(currentGender, currentNames);
            mNames.get(currentNation).put(currentRace, raceNames);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.d("DEBUG", "Failed to read");
        }
        Log.d("DEBUG", "Oh man we finished...");

    }

    */

    private Map<String, List<String>> resetRaceNames()
    {
        Map<String, List<String>> raceNames = new HashMap<String, List<String>>();

        return raceNames;
    }

    private String[] getCurrentInfo(String line)
    {
        String race, gender, nation;
        String[] info = line.split(" ");
        if(info[0].toLowerCase().contains("male"))
        {
            gender = info[0];
            race = info[1];
        }
        else
        {
            gender = info[1];
            race = info[0];
        }
        if(info.length > 2)
        {
            nation = info[2];
        }
        else
        {
            nation = "None";
            info = Arrays.copyOf(info, info.length +1 );
        }
        info[0] = race;
        info[1] = gender;
        info[2]  = nation;
        return info;
    }
}