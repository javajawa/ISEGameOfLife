/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.models;

/**
 *
 * @author george
 */
public class Tuple<K, V> {
    private K key;
    private V value;

    public Tuple(){
        //Do nothing
    }

    public Tuple(K key, V value){
        this.key = key;
        this.value = value;
    }

    public K getKey(){
        return this.key;
    }

    public V getValue(){
        return this.value;
    }

    public void setValue(V newValue){
        this.value = newValue;
    }
}
