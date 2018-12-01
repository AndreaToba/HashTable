import java.io.*;
import java.util.*;

//alpha: 0.1, 0.5, 0.8, 0.9, 1.0
//50k in 50k -> 1.0
//the less table take up 
//crowded if smaller so more collisions so slower

public class HashTable 
{
    private static int collisionCounter = 0;
    private static int finderCounter = 0;
    private static int notFoundCounter = 0;

    public static void main(String[] args) throws FileNotFoundException 
    {
        ArrayList <String> dataSet = new ArrayList<>(50000);
        Scanner kb = new Scanner(new File("Large Data Set.txt"));
        while (kb.hasNextLine()) 
        {
            String input = kb.nextLine();
            dataSet.add(input);
        }
        kb.close();

        ArrayList <String> sSearch = new ArrayList<>();
        kb = new Scanner(new File("Successful Search.txt"));
        while (kb.hasNextLine()) 
        {
            String input = kb.nextLine();
            sSearch.add(input);
        }
        kb.close();

        ArrayList <String> uSearch = new ArrayList<>();
        kb = new Scanner(new File("Unsuccessful Search.txt"));
        while (kb.hasNextLine()) 
        {
            String input = kb.nextLine();
            uSearch.add(input);
        }
        kb.close();

        //HashTable hashTable = new HashTable(500009); // α = 0.1
        //HashTable hashTable = new HashTable(100003); // α = 0.5
        //HashTable hashTable = new HashTable(62501); // α = 0.8
        //HashTable hashTable = new HashTable(55579); // α = 0.9
        HashTable hashTable = new HashTable(50021); // α = 1.0

        long start = System.currentTimeMillis();
        double avgInsert = 0;
        int avgProbe = 0;
        for(int i = 0; i < 50000; i++)
        {
            String input = dataSet.get(i);
            String[] keyValue = input.split(" ");
            
            collisionCounter = 0; //
            long s = System.currentTimeMillis();
            hashTable.put(Integer.parseInt(keyValue[0]), (keyValue[1] + keyValue[2]).trim());
            long e = System.currentTimeMillis();
            avgProbe += collisionCounter; //
            avgInsert += (e-s);
           
        }
        avgProbe = avgProbe / 50000; //?
        avgInsert = avgInsert / 50000;
        long stop = System.currentTimeMillis();

        System.out.println("Time: " + avgInsert + "\nCollisions: " + avgProbe); //time = (stop - start)      avgInsert

        // successfull search
        start = System.currentTimeMillis();
        long avgTimeS = 0;
        int avgProbeS = 0;
        for(int i = 0; i < sSearch.size(); i++)
        {
            String input = sSearch.get(i);
            String[] keyValue = input.split(" ");
            Integer key = Integer.parseInt(keyValue[0]);
            
            finderCounter = 0;
            long s = System.currentTimeMillis();
            hashTable.get(key);
            long e = System.currentTimeMillis();
            avgProbeS += finderCounter;
            avgTimeS += (e-s);
        }
        avgProbeS = avgProbeS / sSearch.size();
        avgTimeS = avgTimeS / sSearch.size();
        stop = System.currentTimeMillis();

        System.out.println("\nTime: " + (stop - start) + "\nObjects Checked: " + avgProbeS);

        // unsuccessfull search
        start = System.currentTimeMillis();
        long avgTimeU = 0;
        int avgProbeU = 0;
        for(int i = 0; i < uSearch.size(); i++)
        {
            String input = uSearch.get(i);
            String[] keyValue = input.split(" ");
            Integer key = Integer.parseInt(keyValue[0]);
            
            notFoundCounter = 0;
            long s = System.currentTimeMillis();
            hashTable.get(key);
            long e = System.currentTimeMillis();
            avgProbeU += notFoundCounter;
            avgTimeU += (e-s);
        }
        avgProbeU = avgProbeU / uSearch.size();
        avgTimeU = avgTimeU / uSearch.size();
        stop = System.currentTimeMillis();
        
        System.out.println("\nTime: " + (stop - start) + "\nObjects Checked: " + avgProbeU);
        
        //  50,000 / load factor = table size, make table size the closest prime number so divide by prime
        // 0.1 --> 500,009
        // 0.5 --> 100,003
        // 0.8 --> 62,501
        // 0.9 --> 55,579
        // 1.0 --> 50,021
        //double loadFactor = 0.1; 
        //int tableSize = (int)(50000 / loadFactor);

    }

    
    
    private Node[] table;
    private int size = 0;

    public HashTable() 
    {
        table = new Node[101];
    }

    public HashTable(int size) 
    {
        table = new Node[size];
    }

    public Object put(Object key, Object value) 
    {
        //Object check = get(key); //if key already exists inside table //would have to look thru whole table
        if (size + 1 < table.length) //if not null override OR if enough room left in size to add to table
        { 
            int index = key.hashCode() % table.length; //calculate indx
            if (table[index] == null) //if location empty or not
            { 
                size++;
                table[index] = new Node(key, value); //if empty makes new node
                return null;
            } 
            else 
            {
                if (table[index].key.equals(key)) //if equal to each other at that location
                { 
                    Object prevValue = table[index].value; //string prev value to return
                    table[index].value = value; //override
                    return prevValue;
                } 
                else 
                {
                    int repeatIndex = index;
                    do //linear probing
                    { 
                        collisionCounter++;
                        if (table[index] == null) //if null done probing
                        { 
                            size++;
                            table[index] = new Node(key, value); //make node and return
                            return null;
                        } 
                        else if (table[index].removed) //if removed location
                        { 
                            size++;
                            table[index] = new Node(key, value); //same
                            int repeatIndex2 = index;
                            index++; //will mess up indx
                            if(index >= table.length) //if go over bounds reset
                            { 
                                index = 0;
                            }
                            do //have to continue searching for duplicate
                            { 
                                if (table[index] == null) //if empty and no duplicates found
                                { 
                                    return null;
                                } 
                                else if (table[index].key.equals(key)) 
                                {
                                    table[index].removed = true;
                                    size--;
                                    return null;
                                }
                                index++;
                                if (index >= table.length) 
                                {
                                    index = 0;
                                }
                            } while (repeatIndex2 != index);
                            return null;
                        }
                        index++;
                        if (index >= table.length) 
                        {
                            index = 0;
                        }
                    }
                    while (repeatIndex != index);
                }
            }
        }
        return null;
    }

    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        for (Node node : table) 
        {
            if (node != null) 
            {
                if (node.removed) 
                {
                    sb.append("dummy\n");
                } 
                else 
                {
                    sb.append(node.key).append(": ").append(node.value).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public Object remove(Object key) 
    {
        int index = key.hashCode() % table.length; //calc indx
        if(table[index] == null)
        {
            return null;
        }
        if (!table[index].removed && key.equals(table[index].key)) //if not removed and equal, will now remove
        { 
            table[index].removed = true;
            size--;
            return table[index].value; //return removed
        } 
        else //check linear probing to find...
        { 
            int repeatIndex = index;
            do 
            { 
                if (table[index] == null) //empty
                { 
                    return null;
                } 
                else if (!table[index].removed && table[index].key.equals(key)) //wasn't removed and keys equal
                { 
                    table[index].removed = true;
                    size--;
                    return table[index].value;
                }
                index++;
                if (index >= table.length) //circular arrau
                {
                    index = 0;
                }
            }
            while (repeatIndex != index);
        }
        return null;
    }

    public Object get(Object key)
    {
        int index = key.hashCode() % table.length;
        if(table[index] == null)
        {
            return null;
        }
        if (!table[index].removed && key.equals(table[index].key)) //if wasn't removed and keys equal
        { 
            return table[index].value;
        } 
        else 
        {
            int repeatIndex = index;
            do 
            {
                finderCounter++;
                notFoundCounter++;
                if (table[index] == null) 
                {
                    return null;
                } 
                else if (!table[index].removed && table[index].key.equals(key)) 
                {
                    return table[index].value;
                }
                index++;
                if (index >= table.length) 
                {
                    index = 0;
                }
            }
            while (repeatIndex != index);
        }
        return null;
    }

    
    
    public class Node 
    {
        public Object key, value;
        boolean removed = false;

        public Node() 
        {
            key = null;
            value = null;
        }

        public Node(Object key, Object value) 
        {
            this.key = key;
            this.value = value;
        }

        public String toString() 
        {
            return key.toString() + ": " + value.toString();
        }
    }
}
