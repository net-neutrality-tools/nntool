export interface UserSettings {
    allow_query_uuid?: boolean;
    allow_set_uuid?: boolean;
    shown: {
        force_ip4: boolean;
        invisible: boolean;
        anonymous_mode: boolean;
        no_anonymize_before_delete_user?: boolean;
        delete_user: boolean;
        client_uuid?: boolean;
    };
}