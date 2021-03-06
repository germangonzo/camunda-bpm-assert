package org.camunda.bpm.engine.test.assertions.cmmn;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.engine.test.assertions.cmmn.CmmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.cmmn.CmmnAwareTests.caseService;
import static org.camunda.bpm.engine.test.assertions.cmmn.CmmnAwareTests.complete;

import org.camunda.bpm.engine.runtime.CaseInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.helpers.Failure;
import org.camunda.bpm.engine.test.assertions.helpers.ProcessAssertTestCase;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTaskAssertIsAvailableTest extends ProcessAssertTestCase {

  public static final String TASK_A = "PI_TaskA";
  public static final String TASK_B = "PI_TaskB";
  public static final String USER_TASK = "UserTask_1";
  public static final String CASE_KEY = "Case_ProcessTaskAssertIsAvailableTest";

  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

  @Test
  @Deployment(resources = { "cmmn/ProcessTaskAssertIsAvailableTest.cmmn", "cmmn/ProcessTaskAssert-calledProcess.bpmn" })
  public void testIsAvailable_Success() {
    // Given
    // case model is deployed
    // When
    CaseInstance caseInstance = givenCaseIsCreated();
    // Then
    assertThat(caseInstance).processTask(TASK_B).isAvailable();
  }

  @Test
  @Deployment(resources = { "cmmn/ProcessTaskAssertIsAvailableTest.cmmn", "cmmn/ProcessTaskAssert-calledProcess.bpmn" })
  public void testIsAvailable_Failure() {
    // Given
    final CaseInstance caseInstance = givenCaseIsCreated();
    // When
    complete(task(USER_TASK, calledProcessInstance(caseInstance)));
    // Then
    expect(new Failure() {
      @Override
      public void when() {
        assertThat(caseInstance).processTask(TASK_B).isAvailable();
      }
    });
  }

  private ProcessInstance calledProcessInstance(CaseInstance caseInstance) {
    return processInstanceQuery().superCaseInstanceId(caseInstance.getId()).singleResult();
  }

  private CaseInstance givenCaseIsCreated() {
    return caseService().createCaseInstanceByKey(CASE_KEY);
  }
}