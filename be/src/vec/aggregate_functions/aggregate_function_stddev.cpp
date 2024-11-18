// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

#include "vec/aggregate_functions/aggregate_function_stddev.h"

#include <fmt/format.h>

#include <string>

#include "common/logging.h"
#include "vec/aggregate_functions/aggregate_function_simple_factory.h"
#include "vec/aggregate_functions/helpers.h"
#include "vec/data_types/data_type.h"
#include "vec/data_types/data_type_nullable.h"

namespace doris::vectorized {

template <template <typename> class Function, typename Name,
          template <typename, typename, bool> class Data, bool is_stddev>
AggregateFunctionPtr create_function_single_value(const String& name,
                                                  const DataTypes& argument_types,
                                                  const bool result_is_nullable) {
    WhichDataType which(remove_nullable(argument_types[0]));
#define DISPATCH(TYPE)                                                              \
    if (which.idx == TypeIndex::TYPE)                                               \
        return creator_without_type::create<Function<Data<TYPE, Name, is_stddev>>>( \
                argument_types, result_is_nullable);
    FOR_NUMERIC_TYPES(DISPATCH)
#undef DISPATCH

    LOG(WARNING) << fmt::format("create_function_single_value with unknowed type {}",
                                argument_types[0]->get_name());
    return nullptr;
}

AggregateFunctionPtr create_aggregate_function_variance_samp(const std::string& name,
                                                             const DataTypes& argument_types,
                                                             const bool result_is_nullable,
                                                             const AggregateFunctionAttr& attr) {
    return create_function_single_value<AggregateFunctionSampVariance, VarianceSampName, SampData,
                                        false>(name, argument_types, result_is_nullable);
}

AggregateFunctionPtr create_aggregate_function_variance_pop(const std::string& name,
                                                            const DataTypes& argument_types,
                                                            const bool result_is_nullable,
                                                            const AggregateFunctionAttr& attr) {
    return create_function_single_value<AggregateFunctionSampVariance, VarianceName, PopData,
                                        false>(name, argument_types, result_is_nullable);
}

AggregateFunctionPtr create_aggregate_function_stddev_pop(const std::string& name,
                                                          const DataTypes& argument_types,
                                                          const bool result_is_nullable,
                                                          const AggregateFunctionAttr& attr) {
    return create_function_single_value<AggregateFunctionSampVariance, StddevName, PopData, true>(
            name, argument_types, result_is_nullable);
}

AggregateFunctionPtr create_aggregate_function_stddev_samp(const std::string& name,
                                                           const DataTypes& argument_types,
                                                           const bool result_is_nullable,
                                                           const AggregateFunctionAttr& attr) {
    return create_function_single_value<AggregateFunctionSampVariance, StddevSampName, SampData,
                                        true>(name, argument_types, result_is_nullable);
}

void register_aggregate_function_stddev_variance_pop(AggregateFunctionSimpleFactory& factory) {
    factory.register_function_both("variance", create_aggregate_function_variance_pop);
    factory.register_alias("variance", "var_pop");
    factory.register_alias("variance", "variance_pop");
    factory.register_function_both("stddev", create_aggregate_function_stddev_pop);
    factory.register_alias("stddev", "stddev_pop");
}

void register_aggregate_function_stddev_variance_samp_old(AggregateFunctionSimpleFactory& factory) {
    BeExecVersionManager::registe_restrict_function_compatibility("variance_samp");
    BeExecVersionManager::registe_restrict_function_compatibility("stddev_samp");
}

void register_aggregate_function_stddev_variance_samp(AggregateFunctionSimpleFactory& factory) {
    factory.register_function_both("variance_samp", create_aggregate_function_variance_samp);
    factory.register_alias("variance_samp", "var_samp");
    factory.register_function_both("stddev_samp", create_aggregate_function_stddev_samp);
    register_aggregate_function_stddev_variance_samp_old(factory);
}
} // namespace doris::vectorized
