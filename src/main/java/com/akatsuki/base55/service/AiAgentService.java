package com.akatsuki.base55.service;

import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.SubTask;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.akatsuki.base55.constant.AgentConstants.FIRST_DECOMPOSITION;

@Service
public class AiAgentService {

    Map<UUID, AiAgent> agents;

    public AiAgentService(){
        agents = new HashMap<>();
    }

    public AiAgent registerAgent(AiAgent agent){
        agents.put(agent.getAgentId(), agent);
        return agent;
    }

    public SubTask decomposeTask(UUID agentId, String task){
        AiAgent agent = agents.get(agentId);
        if(agent == null){
            throw new IllegalArgumentException("Agent not found"); //needs to create custom exception
        }
        return agent.decomposeTask(task, FIRST_DECOMPOSITION);
    }

}
