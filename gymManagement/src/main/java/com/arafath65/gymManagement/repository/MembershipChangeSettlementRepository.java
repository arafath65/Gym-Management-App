
package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.MembershipChangeSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipChangeSettlementRepository
        extends JpaRepository<MembershipChangeSettlement, Integer> {

    List<MembershipChangeSettlement>
    findByTenant_TenantIdOrderByCreatedAtDesc(Integer tenantId);

    List<MembershipChangeSettlement>
    findByOldMembership_MemberMembershipIdOrderByCreatedAtDesc(
            Integer oldMembershipId
    );

    List<MembershipChangeSettlement>
    findByNewMembership_MemberMembershipIdOrderByCreatedAtDesc(
            Integer newMembershipId
    );
}
