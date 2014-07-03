package rhogenwizard;

import org.eclipse.swt.widgets.Combo;

public class UiUtils
{
    public static void selectByItem(Combo combo, String item)
    {
        if (item != null)
        {
            combo.select(combo.indexOf(item));
        }
    }

    public static void updateCombo(Combo combo, String[] items)
    {
        combo.setEnabled(items.length > 1);

        String oldItem = combo.getText();
        combo.setItems(items);
        combo.select(0);
        selectByItem(combo, oldItem);
    }
}
