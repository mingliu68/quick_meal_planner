package com.mingcapstone.quickmealplanner.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListItemMeasurementTotalDto {
    
    private String measure;

    private double amount;

    private List<String> notes = new ArrayList<>();

    public void addNote(String note) {
        notes.add(note);
    }
}
