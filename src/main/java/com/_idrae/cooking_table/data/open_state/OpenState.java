package com._idrae.cooking_table.data.open_state;

import com._idrae.cooking_table.CookingTableMod;

public class OpenState {
    private static boolean openState = false;

    public OpenState(){
    }

    public static boolean isOpenState() {
        return openState;
    }
    public static void setOpenState(boolean openStateIn) {
        openState = openStateIn;
    }
}
