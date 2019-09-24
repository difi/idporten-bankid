#params.pp
class idporten_bankid::params {
  $artifact_id                                   = 'idporten-bankid'
  $service_name                                  = 'idporten-bankid'
  $server_port                                   = 10004
  $bankid_webaddress_hostname                    = hiera('platform::external_host')
  $bankid_webaddress_ip                          = hiera('common::internet_ip')
  $node_environment                              = hiera('platform::node_environment', '')
  $bankid_enabled                                = true
  $bankid_action                                 = 'auth'
  $bankid_clienttype                             = 'NC'
  $bankid_keystore_file                          = '/etc/opt/idporten-bankid/ID-porten-SpareBank_1_SMN.bid'
  $bankid_keystore_password                      = 'password01'
  $bankid_merchant_name                          = 'ID-porten-SpareBank_1_SMN'
  $bankid_grantedpolicies                        = 'ALL'
  $bankid_logging_enabled                        = false
  $bankid_logging_prop_file                      = '/etc/opt/idporten-bankid/bankid.log4j.properties'
  $bankid_logging_category_name                  = 'bankid'
  $bankid_ocsp_max_time_skew                     = 240000
  $bankid_cors_allow_origin                      = 'https://csfe-preprod.bankid.no'
  $bankid_suppress_broadcast                     = 'N'
  $idporten_install_dir                          = '/opt/idporten-bankid'
  $config_root                                   = '/etc/opt/'
  $log_root                                      = '/var/log/'
  $install_dir                      = '/opt/'
  $module                                        = 'idporten_bankid'
  $application                                   = 'idporten-bankid'
  $context                                       = 'opensso'
  $setup_openam                                  = false
}
