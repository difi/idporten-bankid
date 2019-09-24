class idporten_bankid::deploy inherits idporten_bankid {

  include 'difilib'

  difilib::spring_boot_deploy { $idporten_bankid::application:
    package       => 'no.idporten.bankid',
    artifact      => $idporten_bankid::artifact_id,
    service_name  => $idporten_bankid::service_name,
    install_dir   => "${idporten_bankid::install_dir}${idporten_bankid::application}",
    artifact_type => "war",
  }
}


