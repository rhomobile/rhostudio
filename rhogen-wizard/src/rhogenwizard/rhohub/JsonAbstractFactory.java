package rhogenwizard.rhohub;

import org.json.JSONObject;

public interface JsonAbstractFactory<T>
{
    T getInstance(JSONObject object);
}
