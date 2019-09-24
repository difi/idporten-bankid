class idporten_bankid::test_setup inherits idporten_bankid{

  include platform
  if ($platform::test_setup) {

    wget::fetch { 'download_bankid-keys':
      source             => 'http://static.dmz.local/vagrant/eid/resources/ID-porten-SpareBank_1_SMN.bid',
      destination        => "${idporten_bankid::config_root}${idporten_bankid::application}/ID-porten-SpareBank_1_SMN.bid",
      nocheckcertificate => true,
    }
  }
}