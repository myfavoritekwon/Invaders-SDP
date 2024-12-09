package Test;

import engine.GameSettings;
import engine.GameState;
import engine.ItemManager;
import entity.EnemyShipFormation;
import entity.Ship;
import entity.ShipFactory;
import org.junit.jupiter.api.BeforeEach;

import java.util.AbstractMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ItemManagerTest{
    private ItemManager itemManager;

    @BeforeEach
    void setUp(){
        EnemyShipFormation enemyShips = new EnemyShipFormation(new GameSettings(650, 600, 3, 1),
                new GameState(3, 1, Ship.ShipType.StarDefender, 3, 5, 4, 2, "alert", 3, 6, 1, 2 ,3));
        Ship ship = ShipFactory.create(Ship.ShipType.StarDefender, 300, 500);
        itemManager = new ItemManager(ship, enemyShips, new HashSet<>(), 650, 600, 1);
    }

    //아이템 추가
    @org.junit.jupiter.api.Test
    void addItem() {
        ItemManager.ItemType[] itemTypes = ItemManager.ItemType.values();

        boolean addItemCheck = itemManager.addItem(itemTypes[0]);

        assertTrue(addItemCheck);
    }

    //아이템 2개 이상시 추가 못할 때
    @org.junit.jupiter.api.Test
    void addManyItem() {
        ItemManager.ItemType[] itemTypes = ItemManager.ItemType.values();

        itemManager.addItem(itemTypes[0]);
        itemManager.addItem(itemTypes[0]);
        boolean addItemCheck = itemManager.addItem(itemTypes[0]);

        assertFalse(addItemCheck);
    }

    //아이템 위치 변경
    @org.junit.jupiter.api.Test
    void changeItem(){
        ItemManager.ItemType[] item1 = ItemManager.ItemType.values();
        ItemManager.ItemType[] item2 = ItemManager.ItemType.values();
        itemManager.addItem(item1[0]);
        itemManager.addItem(item2[0]);
        assertTrue(itemManager.swapItems());
    }

    //아이템 넣고 사용하기
    @org.junit.jupiter.api.Test
    void useItem(){
        ItemManager.ItemType[] item1 = ItemManager.ItemType.values();

        itemManager.addItem(item1[0]);

        assertEquals(item1[0], itemManager.useStoredItem());
    }

    //스왑하고 사용하기
    @org.junit.jupiter.api.Test
    void swapAndUseItem(){
        ItemManager.ItemType[] item2 = ItemManager.ItemType.values();
        ItemManager.ItemType[] item3 = ItemManager.ItemType.values();
        itemManager.addItem(item2[1]);
        itemManager.addItem(item3[2]);
        itemManager.swapItems();
        assertEquals(item3[2], itemManager.useStoredItem());
    }

    //Laser 반환 잘 하는지
    @org.junit.jupiter.api.Test
    void LaserTest(){
        ItemManager.ItemType[] item = ItemManager.ItemType.values();
        assertEquals(new AbstractMap.SimpleEntry<>(0, 0), itemManager.useItem(item[6]));
    }
}