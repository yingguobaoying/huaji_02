package com.huaji.galgamebyhuaji.vignaAiFrame.node.repository;

import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaMessageNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;

public interface MsgRepository extends Neo4jRepository<VignaMessageNode, String>, CypherdslStatementExecutor<VignaMessageNode> {
}
