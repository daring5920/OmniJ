package org.mastercoin.test.rpc

import com.google.bitcoin.core.Address
import org.mastercoin.BaseRegTestSpec
import org.mastercoin.consensus.ConsensusComparison
import org.mastercoin.consensus.ConsensusEntry
import org.mastercoin.consensus.ConsensusSnapshot
import org.mastercoin.consensus.ConsensusTool
import org.mastercoin.consensus.MasterCoreConsensusTool
import spock.lang.Shared
import spock.lang.Unroll


/**
 *
 */
class MSCSendToOwnersConsensusComparisonSpec extends BaseRegTestSpec {
    @Shared
    ConsensusTool consensusTool

    @Shared
    ConsensusComparison comparison

    @Shared
    Address fundedAddress

    def setupSpec() {
        // Run once before all tests in this Spec
        consensusTool = new MasterCoreConsensusTool(client)
        def startingBTC = 10.0
        def startingMSC = 1000
        def amountSent = 100
        fundedAddress = createFundedAddress(startingBTC, startingMSC)
        def currencyID = org.mastercoin.CurrencyID.TMSC
        def startingPropBal = getbalance_MP(fundedAddress, currencyID).balance
        ConsensusSnapshot startSnap = consensusTool.getConsensusSnapshot(currencyID)
        sendToOwnersMP(fundedAddress, currencyID, amountSent)
        generateBlock()
        ConsensusSnapshot endSnap = consensusTool.getConsensusSnapshot(currencyID)
        comparison = new ConsensusComparison(startSnap, endSnap)
    }

    def setup() {
        // Run before each test
    }

    @Unroll
    def "#address #after > #before" (String address, ConsensusEntry before, ConsensusEntry after) {
        expect:
        (address != fundedAddress.toString()) && (after.balance > before.balance) ||
                (address == fundedAddress.toString()) && (after.balance < before.balance)

        where:
        [address, before, after] << comparison
    }

}