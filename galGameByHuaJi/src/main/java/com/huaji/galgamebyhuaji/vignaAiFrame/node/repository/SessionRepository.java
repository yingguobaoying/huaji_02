package com.huaji.galgamebyhuaji.vignaAiFrame.node.repository;

import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaSessionNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.support.CypherdslStatementExecutor;

public interface SessionRepository extends Neo4jRepository<VignaSessionNode, String>, CypherdslStatementExecutor<VignaSessionNode> {

}
