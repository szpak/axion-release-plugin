package pl.allegro.tech.build.axion.release.infrastructure.git

import org.gradle.api.logging.Logger
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition
import pl.allegro.tech.build.axion.release.domain.scm.ScmRepository
import pl.allegro.tech.build.axion.release.infrastructure.dry.DryRepository
import spock.lang.Specification

class DryRepositoryTest extends Specification {

    ScmRepository scm = Mock()
    Logger logger = Mock()

    DryRepository dryRepository = new DryRepository(scm, logger)

    def "should not create actual tags in scm"() {
        when:
        dryRepository.tag("dry_tag")

        then:
        0 * scm.tag(_)
    }

    def "should not push anything to scm"() {
        when:
        dryRepository.push("dry_remote_name")

        then:
        0 * scm.push(_)
    }

    def "should not commit anything to scm"() {
        when:
        dryRepository.commit("yo")

        then:
        0 * scm.commit(_)
    }

    def "should return currentPosition from real scm"() {
        given:
        ScmPosition expectedPosition = new ScmPosition("master", "latest", true)
        scm.currentPosition(_) >> expectedPosition

        when:
        ScmPosition currentPosition = dryRepository.currentPosition("dry_prefix")

        then:
        currentPosition == expectedPosition
    }

    def "should check uncommited changes on real scm"() {
        given:
        scm.checkUncommitedChanges() >> true

        expect:
        dryRepository.checkUncommitedChanges()
    }

    def "should check ahead of remote on real scm"() {
        given:
        scm.checkAheadOfRemote() >> true

        expect:
        dryRepository.checkAheadOfRemote()
    }

}
