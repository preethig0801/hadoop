/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.timelineservice.storage.subapplication;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnFamily;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnHelper;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnPrefix;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.GenericConverter;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.LongConverter;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.Separator;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.ValueConverter;
import org.apache.hadoop.yarn.server.timelineservice.storage.flow.Attribute;

/**
 * Identifies partially qualified columns for the sub app table.
 */
public enum SubApplicationColumnPrefix
    implements ColumnPrefix<SubApplicationTable> {

  /**
   * To store TimelineEntity getIsRelatedToEntities values.
   */
  IS_RELATED_TO(SubApplicationColumnFamily.INFO, "s"),

  /**
   * To store TimelineEntity getRelatesToEntities values.
   */
  RELATES_TO(SubApplicationColumnFamily.INFO, "r"),

  /**
   * To store TimelineEntity info values.
   */
  INFO(SubApplicationColumnFamily.INFO, "i"),

  /**
   * Lifecycle events for an entity.
   */
  EVENT(SubApplicationColumnFamily.INFO, "e", true),

  /**
   * Config column stores configuration with config key as the column name.
   */
  CONFIG(SubApplicationColumnFamily.CONFIGS, null),

  /**
   * Metrics are stored with the metric name as the column name.
   */
  METRIC(SubApplicationColumnFamily.METRICS, null, new LongConverter());

  private final ColumnFamily<SubApplicationTable> columnFamily;

  /**
   * Can be null for those cases where the provided column qualifier is the
   * entire column name.
   */
  private final String columnPrefix;
  private final byte[] columnPrefixBytes;
  private final ValueConverter valueConverter;

  /**
   * Private constructor, meant to be used by the enum definition.
   *
   * @param columnFamily that this column is stored in.
   * @param columnPrefix for this column.
   */
  SubApplicationColumnPrefix(ColumnFamily<SubApplicationTable> columnFamily,
      String columnPrefix) {
    this(columnFamily, columnPrefix, false, GenericConverter.getInstance());
  }

  SubApplicationColumnPrefix(ColumnFamily<SubApplicationTable> columnFamily,
      String columnPrefix, boolean compondColQual) {
    this(columnFamily, columnPrefix, compondColQual,
        GenericConverter.getInstance());
  }

  SubApplicationColumnPrefix(ColumnFamily<SubApplicationTable> columnFamily,
      String columnPrefix, ValueConverter converter) {
    this(columnFamily, columnPrefix, false, converter);
  }

  /**
   * Private constructor, meant to be used by the enum definition.
   *
   * @param columnFamily that this column is stored in.
   * @param columnPrefix for this column.
   * @param converter used to encode/decode values to be stored in HBase for
   * this column prefix.
   */
  SubApplicationColumnPrefix(ColumnFamily<SubApplicationTable> columnFamily,
      String columnPrefix, boolean compondColQual, ValueConverter converter) {
    this.valueConverter = converter;
    this.columnFamily = columnFamily;
    this.columnPrefix = columnPrefix;
    if (columnPrefix == null) {
      this.columnPrefixBytes = null;
    } else {
      // Future-proof by ensuring the right column prefix hygiene.
      this.columnPrefixBytes =
          Bytes.toBytes(Separator.SPACE.encode(columnPrefix));
    }
  }

  /**
   * @return the column name value
   */
  public String getColumnPrefix() {
    return columnPrefix;
  }

  @Override
  public byte[] getColumnPrefixBytes(byte[] qualifierPrefix) {
    return ColumnHelper.getColumnQualifier(
        this.columnPrefixBytes, qualifierPrefix);
  }

  @Override
  public byte[] getColumnPrefixBytes(String qualifierPrefix) {
    return ColumnHelper.getColumnQualifier(
        this.columnPrefixBytes, qualifierPrefix);
  }

  @Override
  public byte[] getColumnPrefixInBytes() {
    return columnPrefixBytes != null ? columnPrefixBytes.clone() : null;
  }

  @Override
  public byte[] getColumnFamilyBytes() {
    return columnFamily.getBytes();
  }

  @Override
  public ValueConverter getValueConverter() {
    return valueConverter;
  }

  @Override
  public Attribute[] getCombinedAttrsWithAggr(Attribute... attributes) {
    return attributes;
  }

  @Override
  public boolean supplementCellTimeStamp() {
    return false;
  }
}
