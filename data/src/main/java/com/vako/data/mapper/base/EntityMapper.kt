package com.vako.data.mapper.base

interface EntityMapper<Entity, Domain> {
    fun toDomain(entity: Entity): Domain
    fun toEntity(domain: Domain): Entity
}

interface ListEntityMapper<Entity, Domain> : EntityMapper<Entity, Domain> {
    fun toDomainList(entities: List<Entity>): List<Domain> = entities.map(::toDomain)
    fun toEntityList(domains: List<Domain>): List<Entity> = domains.map(::toEntity)
}
