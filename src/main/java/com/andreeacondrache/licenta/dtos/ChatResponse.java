
package com.andreeacondrache.licenta.dtos;

import lombok.Data;
import java.util.List;

@Data
public class ChatResponse {
    private String reply;
    private boolean success;
    private List<TaskDTO> tasks;
}
