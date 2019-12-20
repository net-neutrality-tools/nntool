/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nntool.client.v2.task;

import java.util.concurrent.Callable;

import at.alladin.nntool.shared.qos.QosMeasurementType;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;

/**
 * 
 * @author lb
 *
 */
public interface QoSTask extends Callable<QoSTestResult>, Comparable<QoSTask> {
	
	/**
	 * 
	 * @return
	 */
	public TaskDesc getTaskDesc();
	
	/**
	 * 
	 * @return
	 */
	public int getPriority();
	
	/**
	 * 
	 * @return
	 */
	public long getQoSObjectiveTestId();
	
	/**
	 * 
	 * @return
	 */
	public int getTestServerPort();
	
	/**
	 * 
	 * @return
	 */
	public int getConcurrencyGroup();
	
	/**
	 * 
	 * @return
	 */
	public String getTestServerAddr();

	/**
	 * 
	 * @return
	 */
	public QosMeasurementType getTestType();
	
	/**
	 * 
	 * @return
	 */
	public boolean needsQoSControlConnection();

	/**
	 * kill the QoSTask, if the task needs specific cleanup
	 */
	public void interrupt();
}
